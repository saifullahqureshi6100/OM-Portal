const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfilePayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    displayName: "Ali Raza",
    examDateMillis: Date.now() + 30 * 86400000,
    dailyTargetHours: 2.5,
    studyDaysPerWeek: 6,
    preferredLanguage: "ENGLISH",
    preferredTeachingMode: "Exam Preparation",
    darkModeEnabled: true,
    voiceEnabled: true,
    leaderboardOptIn: false,
    xp: 120,
    streakDays: 2,
    lastStudyDate: "2026-11-24",
    totalStudyMinutes: 90,
    srsReviewsCount: 4,
    earnedBadges: ["first_step"],
    learnedTopics: ["ops_enterprise_management"],
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

function validTopicProgressPayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    partId: "PART_A",
    masteryState: "LEARNING",
    completed: true,
    bookmarked: false,
    easeFactor: 2.5,
    intervalDays: 1,
    repetitions: 1,
    lapses: 0,
    nextReviewAtMillis: Date.now() + 86400000,
    lastReviewedAtMillis: Date.now() - 1000,
    correctCount: 3,
    attemptCount: 4,
    studyMinutes: 25,
    userNotes: "Pasted lecture notes on Mintzberg configurations.",
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

function validExamAttemptPayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    mode: "MOCK_EXAM",
    totalQuestions: 20,
    correctCount: 15,
    incorrectCount: 4,
    skippedCount: 1,
    scorePercent: 75,
    durationSeconds: 1240,
    xpEarned: 250,
    partACorrect: 5,
    partATotal: 7,
    partBCorrect: 3,
    partBTotal: 4,
    partCCorrect: 4,
    partCTotal: 5,
    partDCorrect: 3,
    partDTotal: 4,
    weakTopicIds: ["ops_opt", "perf_balanced_scorecard"],
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

function validLeaderboardPayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    displayName: "Ali Raza",
    xp: 500,
    streakDays: 5,
    badgeCount: 3,
    masteredCount: 10,
    optedIn: true,
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users are denied across all collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("profile").doc("main").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("topicProgress").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("examHistory").get()
  );
  await assertFails(
    unauthDb.collection("leaderboard").where("optedIn", "==", true).get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile")
      .doc("main")
      .set(validProfilePayload(ALICE_UID))
  );
});

test("UserProfile: owner can create and partially update, rejects spoofing and ghost fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("profile")
    .doc("main");

  await assertSucceeds(profileRef.set(validProfilePayload(ALICE_UID)));

  // Partial update preserving createdAt & userId
  await assertSucceeds(
    profileRef.update({
      xp: 220,
      streakDays: 3,
      leaderboardOptIn: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    profileRef.update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost/extra field (Shadow Update test)
  await assertFails(
    profileRef.update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject profile doc ID other than "main"
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile")
      .doc("secondary")
      .set(validProfilePayload(ALICE_UID))
  );

  // Reject cross-user read/write
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("profile").doc("main").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile")
      .doc("main")
      .update({ xp: 9999, updatedAt: new Date(Date.now() - 1000) })
  );
});

test("TopicProgress: owner create, partial SRS/notes update, and cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const topicRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("topicProgress")
    .doc("ops_enterprise_management");

  await assertSucceeds(topicRef.set(validTopicProgressPayload(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("topicProgress").get());

  // Partial SRS update
  await assertSucceeds(
    topicRef.update({
      masteryState: "MASTERED",
      intervalDays: 6,
      repetitions: 2,
      easeFactor: 2.6,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject invalid partId mutation
  await assertFails(
    topicRef.update({
      partId: "PART_B",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject invalid easeFactor below 1.3
  await assertFails(
    topicRef.update({
      easeFactor: 0.9,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject oversized userNotes > 10000 chars
  await assertFails(
    topicRef.update({
      userNotes: "x".repeat(10001),
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject cross-user read/write
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("topicProgress")
      .doc("ops_enterprise_management")
      .get()
  );
});

test("ExamHistory: owner create/list/delete allowed, updates immutable, cross-user denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const attemptRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("examHistory")
    .doc("attempt_1");

  await assertSucceeds(attemptRef.set(validExamAttemptPayload(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("examHistory").get());

  // Exam history is immutable once created
  await assertFails(attemptRef.update({ scorePercent: 100 }));

  // Cross-user cannot read or delete
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("examHistory").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("examHistory")
      .doc("attempt_1")
      .delete()
  );
});

test("Leaderboard: opt-in query alignment, visibility, and owner-only write", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  // Alice opts in
  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(validLeaderboardPayload(ALICE_UID))
  );

  // Bob opts out
  await assertSucceeds(
    bobDb
      .collection("leaderboard")
      .doc(BOB_UID)
      .set(validLeaderboardPayload(BOB_UID, { displayName: "Bob", optedIn: false }))
  );

  // Filtered query succeeds for optedIn == true
  await assertSucceeds(
    bobDb.collection("leaderboard").where("optedIn", "==", true).get()
  );

  // Unfiltered list query MUST fail (rules are not filters)
  await assertFails(bobDb.collection("leaderboard").get());

  // Alice cannot read Bob's opted-out leaderboard doc
  await assertFails(aliceDb.collection("leaderboard").doc(BOB_UID).get());

  // Bob cannot overwrite Alice's leaderboard entry
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).update({ xp: 0, updatedAt: new Date(Date.now() - 1000) })
  );

  // Owner can delete own leaderboard entry when opting out
  await assertSucceeds(bobDb.collection("leaderboard").doc(BOB_UID).delete());
});
