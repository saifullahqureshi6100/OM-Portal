const PARTS = [
  {id:'A', name:'Operations Management', weight:35, topics:[
    'Enterprise management: PESTEL, legal and ethical environment',
    'Digital transformation and ESG integration',
    'Mintzberg Effective Organization and strategic operations',
    'Job, batch and flow production methods',
    '3D printing',
    'Plant maintenance',
    'Resource transformation',
    'MRP, MRP II, ERP, OPT',
    'Capacity management',
    'TQM, Kaizen, Lean Production, TPM, TQMEX',
    'AI quality control and service quality',
    'ISO standards including ISO 56005'
  ]},
  {id:'B', name:'Human Resource Management', weight:20, topics:[
    'Workforce planning','Recruitment and selection','Training and development','Performance and employee relations'
  ]},
  {id:'C', name:'Marketing Management', weight:25, topics:[
    'Marketing concepts and strategy','Customer and market analysis','Segmentation, targeting and positioning','Marketing mix and digital marketing'
  ]},
  {id:'D', name:'Performance Management', weight:20, topics:[
    'Performance measurement','KPIs and targets','Performance evaluation','Continuous improvement'
  ]}
];

const MCQS = [
  {q:'Which part of M3 has the highest stated exam weight?', a:['Operations Management','Marketing Management','Human Resource Management','Performance Management'], correct:0, why:'Operations Management carries 35%.'},
  {q:'Which production method is commonly associated with continuous, standardized high-volume output?', a:['Job production','Flow production','One-off production','Project production'], correct:1, why:'Flow production is arranged for a continuous sequence of standardized work.'},
  {q:'MRP is primarily used to plan:', a:['Materials requirements','Marketing campaigns','Employee salaries','Customer complaints'], correct:0, why:'MRP means Material Requirements Planning.'},
  {q:'Kaizen refers to:', a:['Continuous improvement','Maximum inventory','One-time restructuring','External auditing'], correct:0, why:'Kaizen is the practice of continuous, incremental improvement.'}
];

const URDU_NOTES = {
  'A-0':'انٹرپرائز مینجمنٹ میں ادارے کے اندرونی اور بیرونی ماحول کو سمجھنا شامل ہے۔ PESTEL چھ عوامل کا تجزیہ کرتا ہے: سیاسی، معاشی، سماجی، ٹیکنالوجیکل، ماحولیاتی اور قانونی۔ قانونی تقاضے اور اخلاقی اصول ادارے کو ذمہ دارانہ فیصلے کرنے میں مدد دیتے ہیں۔ امتحان میں ہر عنصر کی تعریف اور کاروبار پر اس کے اثر کی مثال تیار کریں۔',
  'A-1':'ڈیجیٹل ٹرانسفارمیشن کا مطلب ہے ٹیکنالوجی استعمال کرکے ادارے کے کام، خدمات اور فیصلوں کو بہتر بنانا۔ ESG میں ماحولیات، سماجی ذمہ داری اور گورننس شامل ہیں۔ ایک اچھا ادارہ صرف منافع نہیں دیکھتا بلکہ ماحول، لوگوں اور شفاف انتظام کو بھی اہمیت دیتا ہے۔',
  'A-2':'مینٹزبرگ کے مطابق مؤثر تنظیم وہ ہوتی ہے جس کا ڈھانچہ اس کے کام، ماحول اور حکمتِ عملی کے مطابق ہو۔ اس موضوع میں تنظیمی ساخت اور آپریشنز کے درمیان تعلق سمجھیں۔ جواب میں واضح کریں کہ درست ذمہ داریاں، رابطہ اور نگرانی کارکردگی کو بہتر بناتے ہیں۔',
  'A-3':'جاب پروڈکشن میں ہر آرڈر یا کام الگ نوعیت کا ہو سکتا ہے۔ بیچ پروڈکشن میں ایک جیسی اشیا ایک گروپ یا بیچ میں بنتی ہیں۔ فلو پروڈکشن میں مصنوعات مسلسل مرحلہ وار تیار ہوتی ہیں۔ امتحان کے لیے تینوں کا حجم، لچک، لاگت اور مثال کے لحاظ سے فرق یاد کریں۔',
  'A-4':'تھری ڈی پرنٹنگ میں ڈیجیٹل ڈیزائن کی مدد سے مواد کی تہہ در تہہ شکل بنا کر چیز تیار کی جاتی ہے۔ اس سے پیچیدہ ڈیزائن، پروٹوٹائپ اور کم تعداد میں حسبِ ضرورت پیداوار ممکن ہوتی ہے۔ جواب میں فوائد کے ساتھ لاگت، رفتار اور مواد کی حدود بھی بیان کریں۔',
  'A-5':'پلانٹ مینٹیننس سے مراد مشینوں، آلات اور فیکٹری کی دیکھ بھال ہے تاکہ خرابی اور بندش کم ہو۔ پریوینٹو مینٹیننس خرابی آنے سے پہلے مقررہ وقت پر کیا جاتا ہے، جبکہ بریک ڈاؤن مینٹیننس خرابی کے بعد ہوتا ہے۔ مقصد حفاظت، دستیابی اور معیار بہتر رکھنا ہے۔',
  'A-6':'ریسورس ٹرانسفارمیشن میں ادارہ ان پٹس کو تبدیل کرکے آؤٹ پٹ بناتا ہے۔ ان پٹس میں مواد، محنت، سرمایہ، معلومات اور مشینیں شامل ہو سکتی ہیں۔ تبدیلی کے عمل کے بعد مصنوعات یا خدمات حاصل ہوتی ہیں۔ امتحان میں Input، Transformation اور Output کی مثال ضرور دیں۔',
  'A-7':'MRP مواد کی ضرورت اور وقت کا حساب لگانے میں مدد دیتا ہے۔ MRP II اس منصوبہ بندی کو وسیع کرکے دیگر پیداواری وسائل کو بھی شامل کرتا ہے۔ ERP ادارے کے مختلف شعبوں کا ڈیٹا ایک مربوط نظام میں لاتا ہے۔ OPT کا مقصد محدود وسائل یا bottleneck کو بہتر استعمال کرنا ہے۔',
  'A-8':'کیپیسٹی مینجمنٹ کا مطلب ہے ادارے کی دستیاب پیداواری یا سروس صلاحیت کو طلب کے مطابق منصوبہ بند کرنا۔ بہت کم صلاحیت سے آرڈرز تاخیر کا شکار ہوتے ہیں، جبکہ بہت زیادہ صلاحیت سے وسائل ضائع ہو سکتے ہیں۔ طلب کی پیش گوئی اور وسائل کی منصوبہ بندی اہم ہیں۔',
  'A-9':'TQM معیار کو ادارے کی ہر سرگرمی کی ذمہ داری بناتا ہے۔ Kaizen مسلسل چھوٹی بہتری پر توجہ دیتا ہے۔ Lean ضیاع کم کرتا ہے۔ TPM مشینوں کی مجموعی مؤثر دیکھ بھال سے متعلق ہے۔ ان تصورات کا مشترکہ مقصد بہتر معیار، کم ضیاع اور قابلِ اعتماد عمل ہے۔',
  'A-10':'AI کوالٹی کنٹرول میں مصنوعی ذہانت ڈیٹا، تصاویر یا سینسرز کی مدد سے خرابی جلد شناخت کر سکتی ہے۔ سروس کوالٹی میں گاہک کو دی جانے والی خدمت کی درستگی، رفتار، اعتماد اور تجربہ اہم ہوتے ہیں۔ جواب میں ٹیکنالوجی کے فائدے اور انسانی نگرانی کی ضرورت بھی لکھیں۔',
  'A-11':'ISO معیارات اداروں کو منظم طریقۂ کار اور مستقل معیار قائم کرنے میں مدد دیتے ہیں۔ ہر ISO معیار کا مقصد مختلف ہو سکتا ہے، اس لیے اپنے نصاب میں دیے گئے معیار کی مکمل تعریف اور اطلاق اسی کتاب کے مطابق تیار کریں۔ ISO 56005 کا نام اور نصابی سیاق خاص طور پر یاد رکھیں۔',
  'B-0':'ورک فورس پلاننگ میں ادارہ یہ اندازہ لگاتا ہے کہ مستقبل میں کتنے ملازمین اور کون سی مہارتیں درکار ہوں گی۔ مقصد صحیح وقت پر صحیح افراد کو دستیاب رکھنا ہے۔',
  'B-1':'ریکروٹمنٹ امیدواروں کو تلاش کرنے کا عمل ہے، جبکہ سلیکشن ان میں سے موزوں امیدوار منتخب کرنے کا مرحلہ ہے۔ معیار، شفافیت اور ملازمت کی ضروریات کو سامنے رکھیں۔',
  'B-2':'ٹریننگ موجودہ کام بہتر کرنے کے لیے مہارت دیتی ہے، جبکہ ڈیولپمنٹ مستقبل کی ذمہ داریوں کے لیے صلاحیت بناتی ہے۔',
  'B-3':'کارکردگی اور ملازم تعلقات میں اہداف، فیڈبیک، منصفانہ جائزہ اور مؤثر رابطہ اہم ہیں۔',
  'C-0':'مارکیٹنگ میں گاہک کی ضرورت سمجھنا اور ایسی قدر پیش کرنا شامل ہے جو ادارے اور گاہک دونوں کے لیے مفید ہو۔',
  'C-1':'کسٹمر اور مارکیٹ تجزیے میں گاہکوں کی ضروریات، حریفوں، رجحانات اور مارکیٹ کے مواقع کا مطالعہ کیا جاتا ہے۔',
  'C-2':'Segmentation مارکیٹ کو گروپس میں تقسیم کرنا ہے، Targeting مناسب گروپ منتخب کرنا ہے، اور Positioning گاہک کے ذہن میں واضح مقام بنانا ہے۔',
  'C-3':'Marketing Mix عام طور پر پروڈکٹ، قیمت، جگہ اور پروموشن پر مشتمل ہوتا ہے۔ ڈیجیٹل مارکیٹنگ آن لائن چینلز کے ذریعے گاہک تک پہنچتی ہے۔',
  'D-0':'پرفارمنس میژرمنٹ میں نتائج کو واضح پیمانوں سے جانچا جاتا ہے تاکہ معلوم ہو کہ ادارہ اپنے مقاصد کے کتنے قریب ہے۔',
  'D-1':'KPI یعنی Key Performance Indicator ایسا پیمانہ ہے جو کسی اہم مقصد یا کارکردگی کو ناپتا ہے۔ اچھا KPI واضح، قابلِ پیمائش اور متعلقہ ہوتا ہے۔',
  'D-2':'پرفارمنس ایویلیوایشن میں اصل نتائج کا اہداف سے موازنہ کیا جاتا ہے، فرق کی وجہ معلوم کی جاتی ہے اور بہتری کے اقدامات طے ہوتے ہیں۔',
  'D-3':'مسلسل بہتری میں کام کے طریقوں کا باقاعدہ جائزہ لے کر چھوٹی یا بڑی تبدیلیاں کی جاتی ہیں تاکہ معیار اور کارکردگی بہتر ہو۔'
};

const DEFAULT_STATE = {
  page:'dashboard', activePart:'A', done:{}, quizAnswers:{}, quizSubmitted:false,
  quizHistory:[], bestScore:0, studySeconds:0, studyDays:[], plan:null,
  selectedTopic:'A-0', explanation:null, explanationBusy:false,
  settings:{provider:'openrouter',model:'openrouter/free'}, apiStatus:null,
  videoTopic:'A-0', videoUrl:'', reader:null
};

function loadState(){
  try { return Object.assign({}, DEFAULT_STATE, JSON.parse(localStorage.getItem('omState') || '{}')); }
  catch(e){ return Object.assign({}, DEFAULT_STATE); }
}
let state = loadState();
state.settings = Object.assign({}, DEFAULT_STATE.settings, state.settings || {});
let deferredInstallPrompt = null;
let timerInterval = null;

function saveState(){
  localStorage.setItem('omState', JSON.stringify(state));
  updateDashboardNumbers();
}
function esc(value){
  return String(value == null ? '' : value).replace(/[&<>"']/g, function(c){
    return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];
  });
}
function allTopics(){
  return PARTS.flatMap(function(part){
    return part.topics.map(function(title,index){ return {id:part.id+'-'+index,part:part.id,partName:part.name,title:title,index:index}; });
  });
}
function findTopic(id){ return allTopics().find(function(topic){ return topic.id===id; }) || allTopics()[0]; }
function countDone(partId){
  const list = allTopics().filter(function(t){ return !partId || t.part===partId; });
  return list.filter(function(t){ return !!state.done[t.id]; }).length;
}
function overallPercent(){ return Math.round(countDone()/allTopics().length*100); }
function bestScore(){ return state.bestScore || 0; }
function getStreak(){
  const days = Array.from(new Set(state.studyDays || [])).sort();
  if(!days.length) return 0;
  let streak=0, d=new Date();
  d.setHours(0,0,0,0);
  const today = localDateKey(d);
  if(days.indexOf(today)===-1) d.setDate(d.getDate()-1);
  while(days.indexOf(localDateKey(d))!==-1){ streak++; d.setDate(d.getDate()-1); }
  return streak;
}
function localDateKey(d){
  return d.getFullYear()+'-'+String(d.getMonth()+1).padStart(2,'0')+'-'+String(d.getDate()).padStart(2,'0');
}
function markStudyDay(){
  const today=localDateKey(new Date());
  state.studyDays=state.studyDays||[];
  if(state.studyDays.indexOf(today)===-1) state.studyDays.push(today);
}
function partProgress(part){
  const done=countDone(part.id), total=part.topics.length;
  return {done:done,total:total,pct:Math.round(done/total*100)};
}
function partTabs(){
  return '<div class="part-tabs">'+PARTS.map(function(p){
    return '<button class="part-tab '+(state.activePart===p.id?'active':'')+'" data-action="part" data-part="'+p.id+'"><span>PART '+p.id+'</span><b>'+p.name+'</b><strong>'+p.weight+'%</strong></button>';
  }).join('')+'</div>';
}
function progressBar(pct){
  return '<div class="progress-track"><span style="width:'+pct+'%"></span></div>';
}
function topicCard(topic){
  const checked=!!state.done[topic.id];
  return '<article class="topic-card '+(checked?'topic-done':'')+'">'
    +'<div class="topic-check"><input aria-label="Mark topic complete" type="checkbox" data-topic-check="'+topic.id+'" '+(checked?'checked':'')+'></div>'
    +'<div class="topic-main"><div class="topic-kicker">PART '+topic.part+' · TOPIC '+(topic.index+1)+'</div><h3>'+esc(topic.title)+'</h3>'
    +'<div class="topic-actions"><button class="btn btn-small" data-action="explain" data-topic="'+topic.id+'">اردو میں سمجھائیں</button>'
    +'<button class="btn btn-small btn-quiet" data-action="read" data-topic="'+topic.id+'">Reading mode</button>'
    +'<button class="btn btn-small btn-quiet" data-action="video-topic" data-topic="'+topic.id+'">Video clips</button></div></div></article>';
}
function renderDashboard(){
  const total=allTopics().length, done=countDone(), mins=Math.floor((state.studySeconds||0)/60);
  return '<section class="welcome-panel"><div><div class="eyebrow">ICMA PAKISTAN · MANAGERIAL LEVEL-1 · M3</div><h1>Your OM Exam Command Center</h1>'
    +'<p>Study with a clear plan, understand topics in Urdu, practise questions and track your preparation. Operations Management receives first priority because it carries the highest stated weight.</p>'
    +'<div class="welcome-actions"><button class="btn btn-primary" data-action="nav" data-page="plan">Build my exam plan</button><button class="btn btn-outline" data-action="nav" data-page="study">Continue studying</button></div></div>'
    +'<div class="welcome-mark"><span>OM</span><small>STUDY SMART</small></div></section>'
    +'<section class="stats-grid">'
    +statCard('Syllabus completed',overallPercent()+'%',' '+done+' of '+total+' topics')
    +statCard('Study time',mins+' min','Tracked on this device')
    +statCard('Current streak',getStreak()+' days','Days with study activity')
    +statCard('Best MCQ score',bestScore()+'%','Your highest recorded result')
    +'</section>'
    +'<section class="section-head"><div><div class="eyebrow">SYLLABUS TRACKER</div><h2>Progress by exam weight</h2></div><button class="text-button" data-action="nav" data-page="study">Open syllabus →</button></section>'
    +'<div class="part-progress-grid">'+PARTS.map(function(p){
      const pr=partProgress(p);
      return '<button class="part-progress-card" data-action="open-part" data-part="'+p.id+'"><div class="part-progress-top"><span>PART '+p.id+'</span><b>'+p.weight+'%</b></div><h3>'+p.name+'</h3>'+progressBar(pr.pct)+'<div class="small-row"><span>'+pr.done+'/'+pr.total+' topics complete</span><strong>'+pr.pct+'%</strong></div></button>';
    }).join('')+'</div>'
    +'<section class="dashboard-lower"><div class="panel"><div class="section-head compact"><div><div class="eyebrow">FOCUS SESSION</div><h2>Study timer</h2></div><span class="live-dot">LOCAL</span></div>'
    +'<div class="timer-display" id="timer-output">'+formatTimer(state.studySeconds||0)+'</div><p class="muted">Use this timer while studying. Your total time is saved on this device.</p>'
    +'<div class="timer-actions"><button class="btn btn-primary" data-action="timer-start">Start session</button><button class="btn btn-outline" data-action="timer-stop">Finish & save</button></div></div>'
    +'<div class="panel plan-teaser"><div class="eyebrow">NEXT BEST STEP</div><h2>'+(state.plan?'Continue your '+state.plan.days+'-day plan':'Create a realistic exam plan')+'</h2><p class="muted">'+(state.plan?'Your daily schedule is saved. Open it to see today’s topics and revision tasks.':'Choose the time you have before the exam and how many hours you can study per day.')+'</p><button class="btn btn-primary" data-action="nav" data-page="plan">'+(state.plan?'View my plan':'Choose a plan')+'</button></div></section>'
    +'<div class="dashboard-note"><strong>Study priority:</strong> Part A (35%), Part C (25%), Part B (20%) and Part D (20%). This sequence follows the weightings supplied for your M3 paper.</div>';
}
function statCard(label,value,note){
  return '<div class="stat-card"><div class="stat-label">'+label+'</div><div class="stat-value">'+value+'</div><div class="stat-note">'+note+'</div></div>';
}
function formatTimer(seconds){
  const h=Math.floor(seconds/3600),m=Math.floor(seconds%3600/60),s=seconds%60;
  return (h?String(h).padStart(2,'0')+':':'')+String(m).padStart(2,'0')+':'+String(s).padStart(2,'0');
}
function renderStudy(){
  const part=PARTS.find(function(p){return p.id===state.activePart;})||PARTS[0];
  return '<section class="page-intro"><div class="eyebrow">LEARN · UNDERSTAND · REVISE</div><h1>Study syllabus</h1><p>Tick each topic after studying it. Progress is saved automatically in this browser.</p></section>'
    +partTabs()
    +'<section class="panel study-panel"><div class="section-head"><div><div class="eyebrow">PART '+part.id+' · '+part.weight+'% WEIGHT</div><h2>'+part.name+'</h2></div><div class="part-score">'+partProgress(part).pct+'%<small>complete</small></div></div>'
    +progressBar(partProgress(part).pct)
    +'<div class="topic-list">'+part.topics.map(function(t,i){return topicCard({id:part.id+'-'+i,part:part.id,index:i,title:t});}).join('')+'</div></section>'
    +renderQuiz();
}
function renderQuiz(){
  return '<section class="panel quiz-panel"><div class="section-head"><div><div class="eyebrow">RETRIEVAL PRACTICE</div><h2>Quick MCQ check</h2></div><span class="pill">4 QUESTIONS</span></div>'
    +MCQS.map(function(q,i){
      return '<div class="question-card"><h3>'+ (i+1)+'. '+esc(q.q)+'</h3><div class="choice-grid">'+q.a.map(function(a,j){
        const selected=state.quizAnswers[i]===j,correct=state.quizSubmitted&&j===q.correct,wrong=state.quizSubmitted&&selected&&j!==q.correct;
        return '<button class="choice '+(selected?'selected ':'')+(correct?'correct ':'')+(wrong?'incorrect':'')+'" data-action="answer" data-q="'+i+'" data-choice="'+j+'" '+(state.quizSubmitted?'disabled':'')+'><span>'+String.fromCharCode(65+j)+'</span>'+esc(a)+'</button>';
      }).join('')+'</div>'+(state.quizSubmitted?'<p class="answer-note">'+(state.quizAnswers[i]===q.correct?'✓ Correct. ':'✕ Review. ')+esc(q.why)+'</p>':'')+'</div>';
    }).join('')
    +'<div class="quiz-footer">'+(state.quizSubmitted?'<strong class="quiz-result">Score: '+MCQS.filter(function(q,i){return state.quizAnswers[i]===q.correct;}).length+'/'+MCQS.length+'</strong><button class="btn btn-outline" data-action="quiz-reset">Try again</button>':'<button class="btn btn-primary" data-action="quiz-submit" '+(Object.keys(state.quizAnswers||{}).length<MCQS.length?'disabled':'')+'>Check answers</button>')+'</div></section>';
}
function renderPlan(){
  const plan=state.plan;
  const options=[{days:1,label:'1-day emergency revision',desc:'A rapid final review of every topic and key distinction.'},{days:3,label:'3-day crash plan',desc:'High-priority coverage, practice and a final revision round.'},{days:7,label:'7-day intensive plan',desc:'Balanced topic coverage with daily recall and MCQ practice.'},{days:14,label:'14-day balanced plan',desc:'More time for learning, written practice and spaced revision.'}];
  return '<section class="page-intro"><div class="eyebrow">EXAM PREPARATION</div><h1>Choose your preparation plan</h1><p>Select the time available before your exam. The plan uses the syllabus weightings, gives extra time to Operations Management and schedules revision.</p></section>'
    +'<section class="plan-options">'+options.map(function(o){return '<button class="plan-option '+(plan&&plan.days===o.days?'chosen':'')+'" data-action="choose-plan" data-days="'+o.days+'"><span class="plan-days">'+o.days+'<small>days</small></span><span class="plan-option-copy"><b>'+o.label+'</b><small>'+o.desc+'</small></span><span class="plan-arrow">→</span></button>';}).join('')+'</section>'
    +'<section class="panel plan-config"><div class="section-head compact"><div><div class="eyebrow">MAKE IT PERSONAL</div><h2>Daily study capacity</h2></div></div><div class="form-grid"><label>Hours available each day<select id="plan-hours"><option value="1">1 hour</option><option value="2" selected>2 hours</option><option value="3">3 hours</option><option value="4">4 hours</option><option value="5">5 hours</option><option value="6">6 hours</option><option value="8">8 hours</option></select></label><label>Exam date (optional)<input id="exam-date" type="date" min="'+localDateKey(new Date())+'" value="'+(plan&&plan.examDate?esc(plan.examDate):'')+'"></label></div>'
    +'<button class="btn btn-primary" data-action="generate-plan" '+(!plan?'':'')+'>Generate my study timetable</button></section>'
    +(plan?renderPlanSchedule(plan):'<div class="empty-state"><span>▦</span><h3>Your timetable will appear here</h3><p>Choose a plan above, set your daily study hours and generate your schedule.</p></div>':'');
}
function renderPlanSchedule(plan){
  return '<section class="panel schedule-panel"><div class="section-head"><div><div class="eyebrow">YOUR SAVED TIMETABLE</div><h2>'+plan.days+'-day preparation plan</h2></div><span class="pill">'+plan.hours+' HOURS / DAY</span></div>'
   +'<p class="muted">'+(plan.examDate?'Target exam date: '+esc(plan.examDate):'No exam date entered. You can update it any time.')+'</p>'
   +plan.schedule.map(function(day){
     return '<div class="schedule-day"><div class="day-badge"><span>DAY</span><b>'+day.day+'</b></div><div class="day-content"><div class="day-heading"><h3>'+esc(day.title)+'</h3><span>'+day.minutes+' min target</span></div><ul>'+day.items.map(function(item){return '<li>'+esc(item)+'</li>';}).join('')+'</ul><div class="day-method">Study cycle: Learn → Urdu explanation → close-book recall → MCQs → quick review</div></div></div>';
   }).join('')
   +'<div class="plan-footer"><strong>Final revision rule:</strong> On your last day, revisit weak topics, practise timed answers and review the questions you missed.</div></section>';
}
function createPlan(days,hours,examDate){
  const priority=allTopics().sort(function(a,b){
    const weights={A:35,C:25,B:20,D:20};
    return weights[b.part]-weights[a.part] || a.index-b.index;
  });
  const schedule=[]; let cursor=0;
  for(let d=1;d<=days;d++){
    const remaining=priority.length-cursor;
    const slots=Math.ceil(remaining/(days-d+1));
    let tasks=priority.slice(cursor,cursor+slots); cursor+=slots;
    let items=tasks.map(function(t){return 'Study: '+t.title+' (Part '+t.part+')';});
    if(d===days){items.push('Final recall: list key definitions, compare similar concepts and review weak topics.');}
    else{items.push('Closed-book recall and a short MCQ practice set.');}
    if(d%3===0 || d===days){items.push('Review previously completed topics and correct mistakes.');}
    schedule.push({day:d,title:d===days?'Final revision & exam practice':'Learning and active recall',items:items,minutes:hours*60});
  }
  state.plan={days:days,hours:hours,examDate:examDate||'',schedule:schedule,createdAt:new Date().toISOString()};
  markStudyDay(); saveState(); render();
}
function renderTutor(){
  const topics=allTopics();
  const current=findTopic(state.selectedTopic);
  const ex=state.explanation;
  return '<section class="page-intro"><div class="eyebrow">YOUR BILINGUAL AI STUDY COMPANION</div><h1>Urdu voice tutor</h1><p>Choose a syllabus topic, ask for a simple explanation and listen in Urdu. You can also read the explanation in distraction-free mode.</p></section>'
    +'<section class="tutor-grid"><div class="panel tutor-controls"><label class="field-label">Select a topic<select id="tutor-topic">'+topics.map(function(t){return '<option value="'+t.id+'" '+(t.id===state.selectedTopic?'selected':'')+'>Part '+t.part+' · '+esc(t.title)+'</option>';}).join('')+'</select></label>'
    +'<label class="field-label">What should the tutor focus on? <span class="muted">(optional)</span><textarea id="tutor-question" rows="3" placeholder="مثلاً آسان اردو میں تعریف، مثال اور امتحانی جواب سمجھائیں"></textarea></label>'
    +'<button class="btn btn-primary btn-wide" data-action="ask-tutor" '+(state.explanationBusy?'disabled':'')+'>'+(state.explanationBusy?'Preparing explanation…':'Explain this topic in Urdu')+'</button>'
    +'<div class="tutor-shortcuts"><button class="btn btn-outline" data-action="ask-tutor-simple">Simple Urdu</button><button class="btn btn-outline" data-action="ask-tutor-exam">Exam answer format</button></div>'
    +'<div class="voice-note"><span class="voice-icon">◖</span><p><b>Urdu voice</b><br><small>Uses the Urdu voice installed on your device/browser. For best results, add an Urdu voice in Android Text-to-speech settings.</small></p></div></div>'
    +'<div class="panel explanation-panel"><div class="section-head compact"><div><div class="eyebrow">LESSON EXPLANATION</div><h2>'+(ex&&ex.topicId===current.id?esc(current.title):'Your topic explanation')+'</h2></div><span class="pill">اردو</span></div>'
    +(state.explanationBusy?'<div class="loading-state"><span class="loader"></span><p>آپ کے لیے آسان اردو میں وضاحت تیار کی جا رہی ہے…</p></div>':ex?'<div class="urdu-explanation" dir="rtl">'+esc(ex.text).replace(/\n/g,'<br>')+'</div><div class="explanation-actions"><button class="btn btn-primary" data-action="speak-explanation">▶ اردو میں سنیں</button><button class="btn btn-outline" data-action="read-explanation">Full-screen reading</button><button class="btn btn-quiet" data-action="stop-speech">Stop voice</button></div><div class="source-note">'+(ex.source==='ai'?'AI-generated explanation. Verify detailed syllabus wording against your ICMA course book.':'Built-in Urdu study note. Connect a provider key for interactive AI tutoring.')+'</div>':'<div class="empty-state tutor-empty"><span>✦</span><h3>Choose a topic to begin</h3><p>The tutor will explain the concept in easy Urdu, highlight the exam angle and give you a short example.</p><button class="btn btn-outline" data-action="ask-tutor">Explain selected topic</button></div>')+'</div></section>';
}
function renderVideos(){
  const current=findTopic(state.videoTopic);
  const id=extractYoutubeId(state.videoUrl);
  return '<section class="page-intro"><div class="eyebrow">WATCH · PAUSE · REVISE</div><h1>Video clips mode</h1><p>Find short lessons for the exact OM topic. Search results open on YouTube; paste a video or Shorts link below to play it inside this study screen.</p></section>'
    +'<section class="video-layout"><div class="panel video-player-panel"><div class="video-player">'+(id?'<iframe src="https://www.youtube-nocookie.com/embed/'+id+'" title="Study video" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>':'<div class="video-placeholder"><span>▶</span><b>Your study player</b><small>Paste a YouTube video or Shorts link to play it here.</small></div>')+'</div>'
    +'<label class="field-label">YouTube clip URL<input id="video-url" type="url" value="'+esc(state.videoUrl)+'" placeholder="https://www.youtube.com/watch?v=…"></label><button class="btn btn-primary" data-action="load-video">Play this clip</button>'
    +'<div class="small muted">Use official lectures or reliable educational channels. Check that the clip matches the ICMA syllabus topic.</div></div>'
    +'<div class="panel video-topics"><div class="section-head compact"><div><div class="eyebrow">TOPIC CLIP FINDER</div><h2>Find a lesson</h2></div></div>'
    +'<label class="field-label">Current topic<select id="video-topic">'+allTopics().map(function(t){return '<option value="'+t.id+'" '+(t.id===state.videoTopic?'selected':'')+'>Part '+t.part+' · '+esc(t.title)+'</option>';}).join('')+'</select></label>'
    +'<div class="video-topic-card"><span class="video-tag">SELECTED TOPIC</span><h3>'+esc(current.title)+'</h3><p>Search short explanations, lecture clips and worked examples for this topic.</p><a class="btn btn-primary" target="_blank" rel="noopener" href="'+youtubeSearchUrl(current.title)+'">Search YouTube clips ↗</a></div>'
    +'<div class="video-tip">Tip: Watch one short clip, close it, then explain the topic aloud in your own words. Mark the topic complete after you can recall its main points.</div></div></section>';
}
function renderSettings(){
  const status=state.apiStatus;
  return '<section class="page-intro"><div class="eyebrow">CONNECTIONS & PREFERENCES</div><h1>Portal settings</h1><p>Choose a model provider. API credentials stay on Vercel’s server and are never stored in the public JavaScript bundle.</p></section>'
    +'<section class="panel settings-panel"><div class="section-head"><div><div class="eyebrow">AI MODEL CONNECTION</div><h2>Choose your free-tier provider</h2></div><span class="pill">SERVER-SIDE KEYS</span></div>'
    +'<div class="form-grid"><label>Provider<select id="provider-select"><option value="openrouter" '+(state.settings.provider==='openrouter'?'selected':'')+'>OpenRouter · free model router</option><option value="gemini" '+(state.settings.provider==='gemini'?'selected':'')+'>Google Gemini API · free tier</option></select></label>'
    +'<label>Model<select id="model-select">'+modelOptions(state.settings.provider,state.settings.model)+'</select></label></div>'
    +'<div class="connection-status '+(status&&status[state.settings.provider]?'connected':'not-connected')+'"><span class="status-dot"></span><div><b>'+(status&&status[state.settings.provider]?'Provider key detected on server':'Provider key not configured yet')+'</b><small>'+(status&&status[state.settings.provider]?'The server can attempt AI requests. Free-tier quotas still apply.':'Local Urdu notes and study tools work without a key. Add your own key in Vercel to enable live AI explanations.')+'</small></div></div>'
    +'<div class="key-setup-grid"><a class="key-setup-link" href="https://openrouter.ai/settings/keys" target="_blank" rel="noopener"><b>Get an OpenRouter key ↗</b><small>Use free model routing with the model openrouter/free.</small></a><a class="key-setup-link" href="https://aistudio.google.com/app/apikey" target="_blank" rel="noopener"><b>Get a Gemini API key ↗</b><small>Gemini API offers a free tier for selected models and quotas.</small></a></div>'
    +'<div class="env-instructions"><b>Vercel environment variables</b><p>Open Vercel → your OM-Portal project → Settings → Environment Variables. Add only the key for the provider you selected:</p><code>OPENROUTER_API_KEY</code> or <code>GEMINI_API_KEY</code><p>Set it for Production and Preview, then redeploy. Never paste keys into this page, GitHub files or screenshots.</p></div>'
    +'<div class="settings-foot"><span>Selected provider and model are saved on this device.</span><button class="btn btn-outline" data-action="refresh-status">Check connection</button></div></section>'
    +'<section class="panel settings-panel"><div class="eyebrow">DEVICE & PRIVACY</div><h2>Local study data</h2><p class="muted">Topic completion, study time, quiz scores and plans are saved in this browser. Clearing browser storage will remove this local history.</p><button class="btn btn-quiet" data-action="reset-progress">Reset study progress</button></section>';
}
function modelOptions(provider,selected){
  const list=provider==='gemini'?[['gemini-3.8-flash','Gemini 3.8 Flash · free tier'],['gemini-2.5-flash','Gemini 2.5 Flash · free tier']]:[['openrouter/free','OpenRouter Free · automatic routing'],['nvidia/nemotron-3-ultra:free','Nemotron 3 Ultra · free'],['baidu/ernie-5.5-flash-preview:free','ERNIE 5.5 Flash Preview · free'],['nvidia/nemotron-3.5-super-vl:free','Nemotron 3.5 Super VL · free']];
  return list.map(function(m){return '<option value="'+m[0]+'" '+(selected===m[0]?'selected':'')+'>'+m[1]+'</option>';}).join('');
}
function renderReader(){
  const reader=state.reader;
  if(!reader) return '';
  return '<div class="reader-overlay" id="reader-shell"><div class="reader-toolbar"><span class="reader-brand">OM · READING MODE</span><div class="reader-controls"><button class="btn btn-quiet" data-action="font-down">A−</button><button class="btn btn-quiet" data-action="font-up">A+</button><button class="btn btn-outline" data-action="speak-reader">▶ Listen</button><button class="btn btn-quiet" data-action="stop-speech">Stop</button><button class="btn btn-primary" data-action="close-reader">Exit ✕</button></div></div><article class="reader-content" dir="rtl" style="font-size:'+reader.fontSize+'rem"><div class="eyebrow" dir="ltr">PART '+reader.part+' · FULL-SCREEN LESSON</div><h1>'+esc(reader.title)+'</h1><div class="reader-body">'+esc(reader.text).replace(/\n/g,'<br>')+'</div><div class="reader-footer">پڑھنے کے بعد کتاب بند کریں اور اہم نکات اپنی زبان میں دہرائیں۔</div></article></div>';
}
function render(){
  const app=document.getElementById('app');
  if(state.reader){ app.innerHTML=renderReader(); return; }
  const pages={dashboard:renderDashboard,study:renderStudy,plan:renderPlan,tutor:renderTutor,videos:renderVideos,settings:renderSettings};
  const page=pages[state.page]?state.page:'dashboard';
  state.page=page;
  app.innerHTML='<header class="app-header"><a href="#" class="brand" data-action="nav" data-page="dashboard"><span class="brand-mark">OM</span><span><b>ICMA STUDY PORTAL</b><small>Managerial Level-1 · M3</small></span></a><div class="header-actions"><span class="offline-pill"><i></i> Offline ready</span><button class="icon-btn" aria-label="Settings" data-action="nav" data-page="settings">⚙</button></div></header>'
    +'<div class="app-shell"><aside class="sidebar"><div class="side-caption">STUDY WORKSPACE</div>'+navButton('dashboard','Overview','◫')+navButton('study','Syllabus','▤')+navButton('plan','Exam plans','▦')+navButton('tutor','Urdu voice tutor','◖')+navButton('videos','Video clips','▷')+'<div class="side-divider"></div><div class="side-caption">PREFERENCES</div>'+navButton('settings','AI & settings','⚙')+'<div class="sidebar-progress"><div class="small-row"><span>Overall progress</span><b>'+overallPercent()+'%</b></div>'+progressBar(overallPercent())+'<small>'+countDone()+' of '+allTopics().length+' topics complete</small></div></aside>'
    +'<main class="main-content">'+pages[page]()+'<footer class="app-footer">ICMA M3 · Organizational Management <span>Progress is stored locally on this device</span></footer></main></div>'
    +'<nav class="mobile-nav">'+navButton('dashboard','Home','◫')+navButton('study','Study','▤')+navButton('plan','Plan','▦')+navButton('tutor','Tutor','◖')+navButton('videos','Videos','▷')+'</nav><div id="toast" class="toast" role="status" aria-live="polite"></div>';
  refreshTimerDisplay();
}
function navButton(page,label,icon){
  return '<button class="nav-button '+(state.page===page?'active':'')+'" data-action="nav" data-page="'+page+'"><span>'+icon+'</span><b>'+label+'</b></button>';
}
function showToast(message){
  const el=document.getElementById('toast');
  if(!el) return;
  el.textContent=message; el.classList.add('visible');
  window.clearTimeout(showToast.timer);
  showToast.timer=window.setTimeout(function(){el.classList.remove('visible');},3500);
}
function refreshTimerDisplay(){
  const el=document.getElementById('timer-output'); if(!el) return;
  let seconds=state.studySeconds||0;
  if(state.timerStartedAt) seconds+=Math.floor((Date.now()-state.timerStartedAt)/1000);
  el.textContent=formatTimer(seconds);
}
function startTimer(){
  if(state.timerStartedAt) return;
  state.timerStartedAt=Date.now(); markStudyDay(); saveState(); render();
  if(timerInterval) clearInterval(timerInterval);
  timerInterval=setInterval(refreshTimerDisplay,1000);
  showToast('Focus session started. Good luck!');
}
function stopTimer(){
  if(!state.timerStartedAt){showToast('No active timer.');return;}
  state.studySeconds=(state.studySeconds||0)+Math.max(0,Math.floor((Date.now()-state.timerStartedAt)/1000));
  state.timerStartedAt=null; markStudyDay(); saveState();
  if(timerInterval){clearInterval(timerInterval);timerInterval=null;}
  render(); showToast('Your study time has been saved.');
}
function openReader(topic, text){
  state.reader={topicId:topic.id,part:topic.part,title:topic.title,text:text||URDU_NOTES[topic.id]||'اس موضوع کو اپنی ICMA کورس بک کے مطابق پڑھیں، اہم تعریفیں لکھیں اور ایک عملی مثال تیار کریں۔',fontSize:1.28};
  render();
  const shell=document.getElementById('reader-shell');
  if(shell&&shell.requestFullscreen){shell.requestFullscreen().catch(function(){});}
}
function closeReader(){
  state.reader=null;
  if(document.fullscreenElement&&document.exitFullscreen){document.exitFullscreen().catch(function(){});}
  stopSpeech(); render();
}
function speak(text){
  if(!('speechSynthesis' in window)){showToast('This browser does not support text-to-speech.');return;}
  window.speechSynthesis.cancel();
  const utterance=new SpeechSynthesisUtterance(text);
  utterance.lang='ur-PK'; utterance.rate=0.88; utterance.pitch=1;
  const voices=window.speechSynthesis.getVoices();
  const urduVoice=voices.find(function(v){return /^ur(-|$)/i.test(v.lang);})||voices.find(function(v){return /urdu/i.test(v.name);});
  if(urduVoice) utterance.voice=urduVoice;
  utterance.onerror=function(){showToast('Could not play the Urdu voice. Check the device text-to-speech voice settings.');};
  window.speechSynthesis.speak(utterance);
}
function stopSpeech(){if('speechSynthesis' in window) window.speechSynthesis.cancel();}
async function generateExplanation(question){
  const topic=findTopic(state.selectedTopic);
  state.explanationBusy=true; state.page='tutor'; render();
  const userPrompt='Topic: '+topic.title+'\nPaper: ICMA Pakistan Managerial Level-1, M3 Organizational Management.\nStudent request: '+(question||'Explain this topic in simple Urdu for exam preparation.')+'\n\nWrite the explanation in simple, natural Urdu script (not Roman Urdu). Include: a simple definition, step-by-step explanation, one practical example, 3 exam revision points, and one short self-check question. Keep the explanation focused on the named topic. Do not invent official syllabus details or pretend that an example is an ICMA past-paper question.';
  let textResult='',source='local';
  try{
    const response=await fetch('/api/tutor',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({provider:state.settings.provider,model:state.settings.model,prompt:userPrompt,topic:topic.title})});
    const data=await response.json();
    if(!response.ok) throw new Error(data.error||'AI connection is not configured.');
    textResult=data.answer||''; source='ai';
  }catch(error){
    textResult=URDU_NOTES[topic.id]||'اس موضوع کے لیے آسان اردو نوٹس ابھی دستیاب نہیں ہیں۔ اپنی کورس بک میں دی گئی تعریف پڑھیں، پھر اس کی مثال اور امتحانی اہمیت اپنے الفاظ میں لکھیں۔';
    textResult+='\n\nامتحانی تیاری: اس موضوع کی تعریف یاد کریں، ایک مثال دیں، اور اس سے متعلق اہم فرق یا فائدہ مختصر نکات میں لکھنے کی مشق کریں۔';
    source='local';
  }
  state.explanation={topicId:topic.id,text:textResult,source:source};
  state.explanationBusy=false; markStudyDay(); saveState(); render();
}
async function refreshApiStatus(){
  try{
    const r=await fetch('/api/tutor?status=1',{cache:'no-store'});
    if(!r.ok) throw new Error('Status unavailable');
    state.apiStatus=await r.json(); saveState(); render();
    showToast('AI connection status refreshed.');
  }catch(e){
    state.apiStatus={openrouter:false,gemini:false}; render();
    showToast('Live AI status is unavailable. Local study mode remains ready.');
  }
}
function youtubeSearchUrl(title){
  return 'https://www.youtube.com/results?search_query='+encodeURIComponent(title+' organizational management lecture Urdu');
}
function extractYoutubeId(value){
  try{
    const u=new URL(value);
    if(u.hostname==='youtu.be') return u.pathname.split('/').filter(Boolean)[0]||'';
    if(u.hostname.endsWith('youtube.com')){
      if(u.searchParams.get('v')) return u.searchParams.get('v');
      const parts=u.pathname.split('/').filter(Boolean);
      if(parts[0]==='embed'||parts[0]==='shorts') return parts[1]||'';
    }
  }catch(e){}
  return '';
}
function resetProgress(){
  if(!confirm('Reset all topic completion, study time, plans and quiz history on this device? This cannot be undone.')) return;
  state=Object.assign({},DEFAULT_STATE,{settings:state.settings});
  saveState(); render(); showToast('Local study progress has been reset.');
}
function updateDashboardNumbers(){}

document.getElementById('app').addEventListener('click',async function(event){
  const button=event.target.closest('[data-action]'); if(!button) return;
  const action=button.dataset.action;
  if(action==='nav'){state.page=button.dataset.page;state.reader=null;render();return;}
  if(action==='part'){state.activePart=button.dataset.part;render();return;}
  if(action==='open-part'){state.activePart=button.dataset.part;state.page='study';render();return;}
  if(action==='explain'){const t=findTopic(button.dataset.topic);state.selectedTopic=t.id;state.explanation=null;state.page='tutor';render();generateExplanation('Explain in simple Urdu with an everyday example and exam-focused key points.');return;}
  if(action==='ask-tutor'||action==='ask-tutor-simple'||action==='ask-tutor-exam'){
    const q=document.getElementById('tutor-question');
    const prompt=action==='ask-tutor-simple'?'Explain in very simple Urdu for a beginner, with an everyday example.':action==='ask-tutor-exam'?'Explain in Urdu and then give a concise structured exam answer with headings and key points.':(q?q.value:'');
    generateExplanation(prompt);return;
  }
  if(action==='read'){const topic=findTopic(button.dataset.topic);openReader(topic,URDU_NOTES[topic.id]);return;}
  if(action==='read-explanation'){const topic=findTopic(state.explanation.topicId);openReader(topic,state.explanation.text);return;}
  if(action==='speak-explanation'){if(state.explanation)speak(state.explanation.text);return;}
  if(action==='speak-reader'){if(state.reader)speak(state.reader.text);return;}
  if(action==='stop-speech'){stopSpeech();return;}
  if(action==='close-reader'){closeReader();return;}
  if(action==='font-up'&&state.reader){state.reader.fontSize=Math.min(1.8,state.reader.fontSize+0.1);render();return;}
  if(action==='font-down'&&state.reader){state.reader.fontSize=Math.max(0.95,state.reader.fontSize-0.1);render();return;}
  if(action==='video-topic'){state.videoTopic=button.dataset.topic;state.page='videos';render();return;}
  if(action==='load-video'){const field=document.getElementById('video-url');state.videoUrl=field?field.value.trim():'';if(!extractYoutubeId(state.videoUrl)){showToast('Enter a valid YouTube video or Shorts URL.');return;}render();return;}
  if(action==='timer-start'){startTimer();return;}
  if(action==='timer-stop'){stopTimer();return;}
  if(action==='choose-plan'){state.pendingPlanDays=Number(button.dataset.days);render();document.querySelectorAll('.plan-option').forEach(function(el){el.classList.toggle('chosen',Number(el.dataset.days)===state.pendingPlanDays);});return;}
  if(action==='generate-plan'){
    const days=state.pendingPlanDays||(state.plan&&state.plan.days)||7;
    const hours=Number((document.getElementById('plan-hours')||{}).value||2);
    const examDate=(document.getElementById('exam-date')||{}).value||'';
    createPlan(days,hours,examDate);showToast('Your exam timetable is ready.');return;
  }
  if(action==='answer'){
    if(state.quizSubmitted)return;
    state.quizAnswers[button.dataset.q]=Number(button.dataset.choice);
    render();return;
  }
  if(action==='quiz-submit'){
    if(Object.keys(state.quizAnswers).length<MCQS.length){showToast('Answer every question first.');return;}
    const score=Math.round(MCQS.filter(function(q,i){return state.quizAnswers[i]===q.correct;}).length/MCQS.length*100);
    state.quizSubmitted=true;state.bestScore=Math.max(state.bestScore||0,score);
    state.quizHistory=state.quizHistory||[];state.quizHistory.push({score:score,at:new Date().toISOString()});
    markStudyDay();saveState();render();return;
  }
  if(action==='quiz-reset'){state.quizAnswers={};state.quizSubmitted=false;render();return;}
  if(action==='refresh-status'){refreshApiStatus();return;}
  if(action==='reset-progress'){resetProgress();return;}
});
document.getElementById('app').addEventListener('change',function(event){
  const input=event.target;
  if(input.matches('[data-topic-check]')){
    state.done[input.dataset.topicCheck]=input.checked;markStudyDay();saveState();render();return;
  }
  if(input.id==='tutor-topic'){state.selectedTopic=input.value;state.explanation=null;render();return;}
  if(input.id==='video-topic'){state.videoTopic=input.value;render();return;}
  if(input.id==='provider-select'){
    state.settings.provider=input.value;
    state.settings.model=input.value==='gemini'?'gemini-3.8-flash':'openrouter/free';
    saveState();render();return;
  }
  if(input.id==='model-select'){state.settings.model=input.value;saveState();return;}
});
document.getElementById('app').addEventListener('keydown',function(event){
  if(event.key==='Escape'&&state.reader) closeReader();
});
window.addEventListener('beforeinstallprompt',function(event){
  event.preventDefault();deferredInstallPrompt=event;
  const btn=document.getElementById('install-button');if(btn)btn.hidden=false;
});
window.addEventListener('appinstalled',function(){deferredInstallPrompt=null;showToast('OM Study Portal installed successfully.');});
document.addEventListener('fullscreenchange',function(){
  if(!document.fullscreenElement&&state.reader===null) return;
  if(!document.fullscreenElement&&state.reader){/* Reader remains as a focused in-page view. */}
});
async function installApp(){
  if(deferredInstallPrompt){
    deferredInstallPrompt.prompt();
    await deferredInstallPrompt.userChoice;
    deferredInstallPrompt=null;
  }else{
    showToast('Android Chrome: open ⋮ menu and choose Install app or Add to Home screen.');
  }
}
window.installApp=installApp;
render();
refreshApiStatus();
