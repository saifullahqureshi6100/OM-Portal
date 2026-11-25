package com.example.data

import com.example.model.M3Part
import com.example.model.McqQuestion
import com.example.model.QuestionDifficulty

internal object QuestionBankPartAB {
    private const val STUDY_TEXT_LABEL = "Study Text Practice (ICMA M3)"

    val partAMcqs: List<McqQuestion> = listOf(
        McqQuestion(
            id = "q_a1", part = M3Part.PART_A, topicId = "ops_mintzberg_effective_org",
            topicTitle = "Mintzberg's Effective Organization", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "In Mintzberg's structural configurations, which organizational part is dominant in a Machine Bureaucracy relying on standardization of work processes?",
            options = listOf("Strategic Apex", "Operating Core", "Technostructure", "Support Staff"),
            correctIndex = 2,
            explanation = "Machine Bureaucracy depends on analysts in the Technostructure to design and standardize work processes.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a2", part = M3Part.PART_A, topicId = "ops_mintzberg_effective_org",
            topicTitle = "Mintzberg's Effective Organization", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which Mintzberg configuration relies primarily on 'standardization of skills' and places key power in the Operating Core (such as hospitals or universities)?",
            options = listOf("Simple Structure", "Professional Bureaucracy", "Divisionalized Form", "Adhocracy"),
            correctIndex = 1,
            explanation = "Professional Bureaucracies empower trained specialists in the Operating Core via standardized skills.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a3", part = M3Part.PART_A, topicId = "ops_mrp",
            topicTitle = "MRP (Material Requirements Planning)", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which three core inputs are required to run a Material Requirements Planning (MRP) explosion?",
            options = listOf(
                "Sales budget, Kaizen log, and Balance sheet",
                "Master Production Schedule (MPS), Bill of Materials (BOM), and Inventory Status File",
                "Balanced Scorecard, Job descriptions, and Capacity Utilization",
                "Gantt chart, SERVQUAL survey, and Depreciation schedule"
            ),
            correctIndex = 1,
            explanation = "MRP explodes the MPS using the BOM structure and nets against current Inventory Status records.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a4", part = M3Part.PART_A, topicId = "ops_mrp_ii",
            topicTitle = "MRP II (Manufacturing Resource Planning)", difficulty = QuestionDifficulty.DIFFICULT,
            question = "What distinguishes MRP II from basic MRP?",
            options = listOf(
                "MRP II eliminates the need for a Bill of Materials",
                "MRP II integrates closed-loop Capacity Requirements Planning, labor/machine hours, and financial simulation",
                "MRP II applies strictly to non-profit service organizations",
                "MRP II replaces all human operators with 3D printers"
            ),
            correctIndex = 1,
            explanation = "MRP II adds closed-loop capacity checks (CRP), shop-floor feedback, and financial resource integration to basic materials planning.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a5", part = M3Part.PART_A, topicId = "ops_erp",
            topicTitle = "ERP (Enterprise Resource Planning)", difficulty = QuestionDifficulty.EASY,
            question = "What is the foundational architectural benefit of an Enterprise Resource Planning (ERP) system?",
            options = listOf(
                "Keeping separate unconnected spreadsheets per department",
                "A single unified enterprise database acting as a real-time source of truth across functions",
                "Eliminating all internal financial controls",
                "Restricting access exclusively to warehouse clerks"
            ),
            correctIndex = 1,
            explanation = "ERP unifies Finance, Operations, Supply Chain, Sales, and HR onto a shared real-time database.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a6", part = M3Part.PART_A, topicId = "ops_opt",
            topicTitle = "Optimized Production Technology (OPT)", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "According to Goldratt's Optimized Production Technology (OPT), what happens if a non-bottleneck machine runs continuously at 100% utilization?",
            options = listOf(
                "Total factory throughput increases proportionally",
                "Excess Work-In-Progress (WIP) inventory builds up ahead of the bottleneck without increasing throughput",
                "The bottleneck automatically disappears",
                "Lead times drop to zero"
            ),
            correctIndex = 1,
            explanation = "OPT distinguishes activation from utilization: running non-bottlenecks beyond bottleneck pace only creates excess WIP inventory.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a7", part = M3Part.PART_A, topicId = "ops_opt",
            topicTitle = "Optimized Production Technology (OPT)", difficulty = QuestionDifficulty.DIFFICULT,
            question = "In OPT's Drum-Buffer-Rope (DBR) scheduling mechanism, what does the 'Rope' represent?",
            options = listOf(
                "The speed of the constraint/bottleneck",
                "A protective time or stock cushion before the bottleneck",
                "The communication signal tying raw-material release rate to the bottleneck's consumption",
                "Outsourced delivery trucks"
            ),
            correctIndex = 2,
            explanation = "The Drum is the bottleneck pace, the Buffer protects it from starvation, and the Rope pulls raw material in sync with the Drum.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a8", part = M3Part.PART_A, topicId = "ops_tqmex_model",
            topicTitle = "TQMEX model", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "Which sequence accurately represents the six steps of the TQMEX excellence model?",
            options = listOf(
                "TQM -> ISO 9000 -> 5-S -> BPR -> QCC -> TPM",
                "5-S -> BPR -> QCC -> ISO 9000 -> TPM -> TQM",
                "BPR -> 5-S -> Six Sigma -> ERP -> MRP -> OPT",
                "TPM -> QCC -> 5-S -> Kaizen -> ISO 56005 -> TQM"
            ),
            correctIndex = 1,
            explanation = "The TQMEX roadmap progresses through 5-S, Business Process Re-engineering (BPR), Quality Control Circles (QCC), ISO 9000, TPM, and TQM.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a9", part = M3Part.PART_A, topicId = "ops_lean_production",
            topicTitle = "Lean Production", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which of the seven Lean wastes (Muda) is commonly regarded as the most damaging because it hides and triggers all other wastes?",
            options = listOf("Waiting", "Over-production", "Motion", "Transport"),
            correctIndex = 1,
            explanation = "Over-production generates unnecessary WIP inventory, extra transport, waiting, and hidden defects.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a10", part = M3Part.PART_A, topicId = "ops_tpm",
            topicTitle = "Total Productive Maintenance (TPM)", difficulty = QuestionDifficulty.MEDIUM,
            question = "Overall Equipment Effectiveness (OEE) in Total Productive Maintenance is calculated as:",
            options = listOf(
                "Design Capacity × Labor Hours × Scrap Cost",
                "Availability × Performance × Quality",
                "Preventive Cost ÷ Breakdown Cost",
                "Uptime + Overtime − Setup Time"
            ),
            correctIndex = 1,
            explanation = "OEE = Availability × Performance Rate × Quality Rate.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a11", part = M3Part.PART_A, topicId = "ops_tqm",
            topicTitle = "Total Quality Management (TQM)", difficulty = QuestionDifficulty.MEDIUM,
            question = "Within the PAF Cost of Quality model, warranty claims, product recalls, and lost customer goodwill belong to:",
            options = listOf("Prevention costs", "Appraisal costs", "Internal failure costs", "External failure costs"),
            correctIndex = 3,
            explanation = "Defects discovered after delivery to customers create External Failure costs, the costliest quality category.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a12", part = M3Part.PART_A, topicId = "ops_service_quality",
            topicTitle = "Service quality", difficulty = QuestionDifficulty.EASY,
            question = "In the SERVQUAL (RATER) framework, which dimension measures a firm's ability to perform the promised service dependably and accurately?",
            options = listOf("Tangibles", "Reliability", "Empathy", "Assurance"),
            correctIndex = 1,
            explanation = "Reliability represents consistent, accurate delivery of promised service performance.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a13", part = M3Part.PART_A, topicId = "ops_iso_standards",
            topicTitle = "ISO standards, including ISO 56005", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "What specific domain does ISO 56005 govern within an organization's innovation framework?",
            options = listOf(
                "Food safety hazard critical control points",
                "Tools and methods for Intellectual Property (IP) management in innovation",
                "Boiler pressure vessel inspection",
                "Corporate greenhouse gas accounting only"
            ),
            correctIndex = 1,
            explanation = "ISO 56005 provides guidance and tools for managing Intellectual Property (IP) strategically across the innovation lifecycle.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a14", part = M3Part.PART_A, topicId = "ops_job_batch_flow",
            topicTitle = "Job, batch and flow production", difficulty = QuestionDifficulty.EASY,
            question = "A shipyard building a single custom offshore vessel to unique client specifications uses which production method?",
            options = listOf("Continuous flow production", "Mass production", "Job (project) production", "Just-in-time batching"),
            correctIndex = 2,
            explanation = "Job/project production handles unique, bespoke, low-volume high-variety deliverables.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a15", part = M3Part.PART_A, topicId = "ops_capacity_management",
            topicTitle = "Capacity management", difficulty = QuestionDifficulty.DIFFICULT,
            question = "A bottling line has Design Capacity of 1,000 units/hr, Effective Capacity of 850 units/hr, and Actual Output of 680 units/hr. What is its Efficiency?",
            options = listOf("68%", "80%", "85%", "72%"),
            correctIndex = 1,
            explanation = "Efficiency = Actual Output / Effective Capacity = 680 / 850 = 80% (whereas Utilization = 680 / 1000 = 68%).",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a16", part = M3Part.PART_A, topicId = "ops_3d_printing",
            topicTitle = "3D printing", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which operational advantage best characterizes 3D printing (additive manufacturing) compared with traditional subtractive CNC machining?",
            options = listOf(
                "Faster unit speed for 10 million identical plastic bottle caps",
                "Minimal material waste and rapid on-demand production of complex spare parts without expensive tooling",
                "Complete elimination of CAD software",
                "Zero post-processing ever required"
            ),
            correctIndex = 1,
            explanation = "Additive manufacturing deposits material layer-by-layer only where needed, ideal for prototyping, complex geometries, and spare parts.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a17", part = M3Part.PART_A, topicId = "ops_plant_maintenance",
            topicTitle = "Plant maintenance", difficulty = QuestionDifficulty.MEDIUM,
            question = "Using IoT vibration and thermal sensors on motors to intervene right before bearing failure occurs is an example of:",
            options = listOf("Run-to-failure breakdown maintenance", "Predictive / condition-based maintenance", "Corrective emergency repair", "Zero-maintenance policy"),
            correctIndex = 1,
            explanation = "Predictive maintenance monitors live equipment condition to schedule repairs just before failure.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a18", part = M3Part.PART_A, topicId = "ops_pestel_analysis",
            topicTitle = "PESTEL analysis", difficulty = QuestionDifficulty.EASY,
            question = "A sudden rise in central bank interest rates and PKR exchange-rate volatility fall primarily under which PESTEL category?",
            options = listOf("Social", "Technological", "Economic", "Environmental"),
            correctIndex = 2,
            explanation = "Interest rates, inflation, GDP growth, and exchange rates are Economic macro-environmental forces.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a19", part = M3Part.PART_A, topicId = "ops_esg_integration",
            topicTitle = "ESG integration", difficulty = QuestionDifficulty.MEDIUM,
            question = "Board independence, audit transparency, and anti-bribery controls correspond to which pillar of ESG?",
            options = listOf("Environmental", "Social", "Governance", "Ergonomics"),
            correctIndex = 2,
            explanation = "The Governance ('G') pillar covers board oversight, executive ethics, audit integrity, and shareholder/stakeholder rights.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a20", part = M3Part.PART_A, topicId = "ops_kaizen",
            topicTitle = "Kaizen", difficulty = QuestionDifficulty.EASY,
            question = "How does Kaizen differ from Business Process Re-engineering (BPR)?",
            options = listOf(
                "Kaizen focuses on continuous small incremental improvements involving everyone; BPR pursues radical step-change redesign",
                "Kaizen requires replacing the entire IT architecture every quarter",
                "BPR excludes management participation",
                "Kaizen prohibits employee suggestions"
            ),
            correctIndex = 0,
            explanation = "Kaizen champions ongoing small improvements at Gemba, whereas BPR conducts dramatic clean-slate process redesign.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_a21", part = M3Part.PART_A, topicId = "ops_strategic_operations",
            topicTitle = "Strategic operations", difficulty = QuestionDifficulty.DIFFICULT,
            question = "In operations strategy, 'Order Qualifiers' are best defined as:",
            options = listOf(
                "Bonus features that single-handedly win every tender",
                "Minimum baseline performance criteria a firm must meet just to be considered by customers",
                "Internal accounting codes for purchase orders",
                "Discounts offered strictly to employees"
            ),
            correctIndex = 1,
            explanation = "Order Qualifiers get a supplier onto the customer's shortlist; Order Winners actually secure the contract.",
            sourceLabel = STUDY_TEXT_LABEL
        )
    )

    val partBMcqs: List<McqQuestion> = listOf(
        McqQuestion(
            id = "q_b1", part = M3Part.PART_B, topicId = "hr_employee_motivation",
            topicTitle = "Employee motivation", difficulty = QuestionDifficulty.MEDIUM,
            question = "According to Herzberg's Two-Factor Theory, improving unsafe working conditions and fixing unfair pay policies removes dissatisfaction, because these are:",
            options = listOf("Self-actualization motivators", "Hygiene factors", "Expectancy multipliers", "Theory X bonuses"),
            correctIndex = 1,
            explanation = "Hygiene factors (pay, supervision, working conditions, policies) prevent job dissatisfaction but do not create lasting intrinsic motivation.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b2", part = M3Part.PART_B, topicId = "hr_employee_motivation",
            topicTitle = "Employee motivation", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "In Vroom's Expectancy Theory (Motivation = Expectancy × Instrumentality × Valence), what happens if an employee believes high performance will NOT lead to any promised reward (Instrumentality = 0)?",
            options = listOf(
                "Motivation remains high due to Valence",
                "Motivation drops to zero regardless of how attractive the reward is",
                "Expectancy doubles automatically",
                "Adams Equity resets to 1.0"
            ),
            correctIndex = 1,
            explanation = "Because Vroom's relationship is multiplicative, if Expectancy, Instrumentality, or Valence is zero, overall motivation becomes zero.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b3", part = M3Part.PART_B, topicId = "hr_algorithmic_recruitment_bias",
            topicTitle = "Algorithmic recruitment bias", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "Why can an AI resume-screening tool exhibit systemic gender or demographic bias even when 'gender' is removed from input fields?",
            options = listOf(
                "Because the model learns correlations with proxy variables (like club names or career gaps) present in biased historical hiring data",
                "Because AI models always choose applicants alphabetically",
                "Because Cloud servers only accept PDFs",
                "Because structured interviews prevent computers from booting"
            ),
            correctIndex = 0,
            explanation = "Proxy attributes in skewed historical training datasets allow ML rankers to replicate legacy hiring discrimination.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b4", part = M3Part.PART_B, topicId = "hr_selection",
            topicTitle = "Selection", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which selection bias occurs when an interviewer lets one standout positive trait—such as graduating from a prestigious school—cloud objective evaluation of all other job competencies?",
            options = listOf("Central tendency", "Halo effect", "Recency error", "Leniency bias"),
            correctIndex = 1,
            explanation = "The Halo Effect generalizes one favorable characteristic into an inflated overall impression.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b5", part = M3Part.PART_B, topicId = "hr_training",
            topicTitle = "Training", difficulty = QuestionDifficulty.MEDIUM,
            question = "In Kirkpatrick's 4-level training evaluation model, measuring whether trainees actually apply newly learned behaviors back on the shop floor or office corresponds to:",
            options = listOf("Level 1: Reaction", "Level 2: Learning", "Level 3: Behavior", "Level 4: Results"),
            correctIndex = 2,
            explanation = "Level 3 (Behavior) assesses on-the-job transfer of learning into daily work practices.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b6", part = M3Part.PART_B, topicId = "hr_training",
            topicTitle = "Training", difficulty = QuestionDifficulty.DIFFICULT,
            question = "What is the correct sequence of stages in Kolb's Experiential Learning Cycle?",
            options = listOf(
                "Concrete Experience -> Reflective Observation -> Abstract Conceptualization -> Active Experimentation",
                "Active Experimentation -> Concrete Experience -> Punishment -> Certification",
                "Reaction -> Learning -> Behavior -> Results",
                "Analysis -> Selection -> Decruitment -> Exit"
            ),
            correctIndex = 0,
            explanation = "Kolb's cycle moves through Feeling (Concrete Experience), Watching (Reflective Observation), Thinking (Abstract Conceptualization), and Doing (Active Experimentation).",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b7", part = M3Part.PART_B, topicId = "hr_appraisals",
            topicTitle = "Appraisals", difficulty = QuestionDifficulty.EASY,
            question = "A 360-degree performance appraisal collects feedback from:",
            options = listOf(
                "Only the immediate line manager",
                "External tax auditors exclusively",
                "Multiple sources including supervisor, peers, subordinates, self, and sometimes customers",
                "Automated biometric attendance clocks only"
            ),
            correctIndex = 2,
            explanation = "360-degree feedback triangulates performance perceptions across supervisors, peers, direct reports, and self-assessment.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b8", part = M3Part.PART_B, topicId = "hr_remote_hybrid_policies",
            topicTitle = "Remote and hybrid work policies", difficulty = QuestionDifficulty.MEDIUM,
            question = "What is 'proximity bias' in a hybrid workplace?",
            options = listOf(
                "Paying remote engineers extra for home electricity",
                "Favoring employees who are physically present in the office for promotions and assignments over equally productive remote peers",
                "Installing routers closer to conference rooms",
                "Requiring VPN encryption on laptops"
            ),
            correctIndex = 1,
            explanation = "Proximity bias unfairly rewards visibility ('face time') over measurable output in hybrid setups.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b9", part = M3Part.PART_B, topicId = "hr_mental_health_analytics",
            topicTitle = "Mental health analytics", difficulty = QuestionDifficulty.DIFFICULT,
            question = "Which governance rule is ethically essential when an HR team deploys mental health and burnout analytics?",
            options = listOf(
                "Publish individual employee stress scores on team leaderboards",
                "Use anonymized, aggregate trend data with opt-in consent—never for punitive appraisal or firing decisions",
                "Install continuous webcam emotion tracking without notice",
                "Share medical diagnoses directly with line managers"
            ),
            correctIndex = 1,
            explanation = "Mental health analytics must protect medical privacy using aggregate, anonymized signals aimed strictly at proactive support.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b10", part = M3Part.PART_B, topicId = "hr_decruitment",
            topicTitle = "Decruitment", difficulty = QuestionDifficulty.MEDIUM,
            question = "Which decruitment option reduces headcount with the lowest immediate morale shock?",
            options = listOf(
                "Unannounced mass layoffs",
                "Natural attrition paired with a hiring freeze and internal redeployment",
                "Across-the-board summary dismissals",
                "Outsourcing core leadership overnight"
            ),
            correctIndex = 1,
            explanation = "Attrition, hiring freezes, and reskilling/redeployment minimize involuntary job loss and survivor syndrome.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b11", part = M3Part.PART_B, topicId = "hr_human_capital_theories",
            topicTitle = "Human capital theories", difficulty = QuestionDifficulty.EASY,
            question = "Under Human Capital Theory and the Resource-Based View (VRIO), employee skills and institutional knowledge are viewed as:",
            options = listOf(
                "Purely disposable variable costs",
                "Strategic intangible assets that appreciate with training and experience",
                "Fixed liabilities on the balance sheet",
                "Irrelevant in automated factories"
            ),
            correctIndex = 1,
            explanation = "Human capital theory recognizes investments in knowledge, training, and capabilities as value-generating assets.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_b12", part = M3Part.PART_B, topicId = "hr_compensation",
            topicTitle = "Compensation", difficulty = QuestionDifficulty.MEDIUM,
            question = "What is the key distinction between Job Evaluation and Performance Appraisal?",
            options = listOf(
                "Job evaluation assesses the relative worth of a ROLE to set pay grades; performance appraisal assesses an INDIVIDUAL employee's performance",
                "Job evaluation is only done at termination",
                "Performance appraisal determines statutory minimum wage",
                "They are identical terms"
            ),
            correctIndex = 0,
            explanation = "Job evaluation establishes internal pay equity across positions/roles, while appraisals evaluate how well an individual performs.",
            sourceLabel = STUDY_TEXT_LABEL
        )
    )
}
