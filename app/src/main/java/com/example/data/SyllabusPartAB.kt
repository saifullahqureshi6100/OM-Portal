package com.example.data

import com.example.model.M3Part
import com.example.model.SyllabusTopic
import com.example.model.TopicDifficulty

internal object SyllabusPartAB {
    val partATopics: List<SyllabusTopic> = listOf(
        SyllabusTopic(
            id = "ops_enterprise_management",
            part = M3Part.PART_A,
            title = "Enterprise management",
            subtitle = "Value creation, organizational structures & strategic alignment",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Enterprise management is the coordinated planning, organizing, directing, and controlling of an organization's human, financial, physical, and information resources to deliver sustainable stakeholder value.",
            keyConcepts = listOf(
                "Integration of core functional areas: Operations, HRM, Marketing, Finance, and Strategy.",
                "Levels of management: Strategic (board/C-suite), Tactical (functional managers), Operational (supervisors).",
                "Cross-functional resource allocation and governance frameworks."
            ),
            examples = listOf(
                "A textile conglomerate aligning spinning mill capacity with export marketing targets and cash-flow budgets.",
                "Enterprise-wide risk management linking supply chain disruptions to board-level contingency reserves."
            ),
            revisionNotes = listOf(
                "Link decisions across Strategic, Tactical, and Operational tiers.",
                "Enterprise value depends on efficiency (doing things right) + effectiveness (doing right things)."
            ),
            keyFramework = "Strategic–Tactical–Operational Hierarchy"
        ),
        SyllabusTopic(
            id = "ops_pestel_analysis",
            part = M3Part.PART_A,
            title = "PESTEL analysis",
            subtitle = "Macro-environmental scanning framework",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "PESTEL analysis evaluates six macro-environmental forces—Political, Economic, Social, Technological, Environmental (Ecological), and Legal—that shape an enterprise's opportunities and threats.",
            keyConcepts = listOf(
                "Political: tax policy, trade tariffs, political stability, government subsidies.",
                "Economic: inflation, interest rates, exchange rates, GDP growth, disposable income.",
                "Social: demographics, lifestyle shifts, education, cultural attitudes.",
                "Technological: automation, AI adoption, R&D incentives, digital infrastructure.",
                "Environmental: climate rules, carbon footprint, waste disposal, sustainability.",
                "Legal: labor laws, consumer protection, antitrust, health & safety statutes."
            ),
            examples = listOf(
                "A Pakistani pharmaceutical firm assessing DRAP pricing controls (Legal/Political) and PKR depreciation on API imports (Economic).",
                "Solar manufacturers leveraging import duty exemptions and rising grid tariffs."
            ),
            revisionNotes = listOf(
                "PESTEL examines external macro forces outside direct management control.",
                "Often feeds directly into SWOT Opportunities & Threats."
            ),
            keyFramework = "P-E-S-T-E-L Macro Scan"
        ),
        SyllabusTopic(
            id = "ops_legal_ethical",
            part = M3Part.PART_A,
            title = "Legal and ethical considerations",
            subtitle = "Compliance, corporate governance, CSR & ethical operations",
            difficulty = TopicDifficulty.CORE,
            definition = "Legal considerations require strict compliance with statutory laws and regulations, whereas ethical considerations govern moral conduct, fairness, transparency, and social responsibility beyond minimum legal rules.",
            keyConcepts = listOf(
                "Compliance vs. Ethics: legal is mandatory floor; ethics is voluntary moral standard.",
                "Corporate Social Responsibility (CSR) & stakeholder accountability.",
                "Worker safety, anti-bribery, fair supplier contracts, and product liability.",
                "Whistleblowing protection and ethical supply chain sourcing."
            ),
            examples = listOf(
                "Refusing child labor in supplier tiers even where local enforcement is weak.",
                "Recalling a defective batch proactively before regulators issue a mandatory notice."
            ),
            revisionNotes = listOf(
                "Legal != Ethical: an action can be legal yet unethical (e.g., aggressive tax loopholes or misleading fine print).",
                "Ethical failures destroy brand equity and trigger regulatory fines."
            ),
            keyFramework = "Compliance–Ethics–CSR Spectrum"
        ),
        SyllabusTopic(
            id = "ops_digital_transformation",
            part = M3Part.PART_A,
            title = "Digital transformation",
            subtitle = "Cloud, IoT, AI workflows & digital business models",
            difficulty = TopicDifficulty.CORE,
            definition = "Digital transformation is the fundamental integration of digital technology across all business areas, reshaping operations, culture, and how value is delivered to customers.",
            keyConcepts = listOf(
                "Digitization (analog to digital) vs. Digitalization (process improvement) vs. Digital Transformation (business model & cultural shift).",
                "Key enablers: Cloud computing, IoT sensors, Big Data analytics, Robotic Process Automation (RPA), AI.",
                "Change management barriers: legacy systems, skill gaps, cybersecurity risks."
            ),
            examples = listOf(
                "A bank migrating branch paper workflows to instant mobile onboarding and automated credit scoring.",
                "Smart factory sensors streaming equipment telemetry to cloud dashboards."
            ),
            revisionNotes = listOf(
                "Technology alone is not transformation—people, culture, and process redesign are essential.",
                "Always pair digital rollouts with cybersecurity and change management."
            ),
            keyFramework = "Digitization -> Digitalization -> Transformation"
        ),
        SyllabusTopic(
            id = "ops_esg_integration",
            part = M3Part.PART_A,
            title = "ESG integration",
            subtitle = "Environmental, Social & Governance operational metrics",
            difficulty = TopicDifficulty.CORE,
            definition = "ESG integration embeds Environmental, Social, and Governance criteria into operational strategy, procurement, risk management, and performance reporting to ensure long-term resilience.",
            keyConcepts = listOf(
                "Environmental: emissions (Scope 1–3), water stewardship, circular packaging, energy efficiency.",
                "Social: workplace diversity, fair wages, community impact, occupational health.",
                "Governance: board independence, executive pay transparency, audit integrity, anti-corruption."
            ),
            examples = listOf(
                "A cement plant switching to waste-heat recovery and disclosing carbon intensity to green-bond investors.",
                "Supplier ESG scorecards required prior to tender qualification."
            ),
            revisionNotes = listOf(
                "E = Planet, S = People, G = Board & Controls.",
                "ESG lowers cost of capital and attracts institutional investors."
            ),
            keyFramework = "ESG Triple Lens"
        ),
        SyllabusTopic(
            id = "ops_mintzberg_effective_org",
            part = M3Part.PART_A,
            title = "Mintzberg's Effective Organization",
            subtitle = "5 building blocks & organizational configurations",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Henry Mintzberg modeled organizations around five core parts—Strategic Apex, Middle Line, Operating Core, Technostructure, and Support Staff—which combine into distinct structural configurations.",
            keyConcepts = listOf(
                "5 Basic Parts: Strategic Apex (top leadership), Middle Line (middle managers), Operating Core (frontline producers), Technostructure (analysts/planners standardizing work), Support Staff (HR, legal, PR, canteen).",
                "Core Configurations: Simple Structure (direct supervision), Machine Bureaucracy (standardized work processes), Professional Bureaucracy (standardized skills, e.g., hospitals/universities), Divisionalized Form (standardized outputs), Adhocracy (mutual adjustment for innovation).",
                "Later additions: Missionary (ideology/norms) & Political organization."
            ),
            examples = listOf(
                "Machine Bureaucracy: high-volume automotive assembly plant led by standardized procedures.",
                "Professional Bureaucracy: university or audit firm relying on highly trained specialists in the Operating Core."
            ),
            revisionNotes = listOf(
                "Memorize each configuration's prime coordinating mechanism & key part!",
                "Simple -> Strategic Apex; Machine -> Technostructure; Professional -> Operating Core; Divisional -> Middle Line; Adhocracy -> Support Staff/Mutual Adjustment."
            ),
            keyFramework = "Mintzberg's 5 Building Blocks & Configurations"
        ),
        SyllabusTopic(
            id = "ops_strategic_operations",
            part = M3Part.PART_A,
            title = "Strategic operations",
            subtitle = "4 Vs, order winners vs qualifiers & competitive priorities",
            difficulty = TopicDifficulty.CORE,
            definition = "Strategic operations aligns day-to-day operational capabilities—cost, quality, speed, dependability, and flexibility—with corporate competitive strategy.",
            keyConcepts = listOf(
                "Five Performance Objectives: Quality, Speed, Dependability, Flexibility, Cost.",
                "Hayes & Wheelwright 4-stage operations role: Internally Neutral -> Externally Neutral -> Internally Supportive -> Externally Supportive.",
                "4 Vs Profile: Volume, Variety, Variation in demand, Visibility.",
                "Order Qualifiers (minimum standard to be considered) vs. Order Winners (reason customers pick you)."
            ),
            examples = listOf(
                "High volume & low variety (cement) prioritizes low unit cost; low volume & high variety (bespoke tailoring) prioritizes flexibility.",
                "Fast parcel tracking acting as an order winner in e-commerce."
            ),
            revisionNotes = listOf(
                "High Volume + Low Variety = Low unit cost; Low Volume + High Variety = High unit cost.",
                "Operations strategy translates market requirements into structural & infrastructural decisions."
            ),
            keyFramework = "5 Performance Objectives & 4 Vs"
        ),
        SyllabusTopic(
            id = "ops_job_batch_flow",
            part = M3Part.PART_A,
            title = "Job, batch and flow production",
            subtitle = "Process types along the volume-variety continuum",
            difficulty = TopicDifficulty.CORE,
            definition = "Production processes are categorized by volume and variety into Job (project/unit), Batch, and Flow (mass/continuous) production.",
            keyConcepts = listOf(
                "Job Production: bespoke one-off items, skilled labor, general-purpose tools, high unit cost (e.g., custom bridge, wedding cake).",
                "Batch Production: groups of identical units produced together with changeovers/setup time between batches (e.g., bakery loaves, clothing sizes).",
                "Flow / Mass Production: continuous high-volume standardized output, specialized machinery, low unit cost, high capital expenditure (e.g., bottling line, oil refinery)."
            ),
            examples = listOf(
                "Job: custom ERP software build. Batch: seasonal apparel runs. Flow: carbonated beverage canning."
            ),
            revisionNotes = listOf(
                "Watch changeover time & WIP inventory trade-offs in Batch production.",
                "Flow production requires steady demand and preventive maintenance because one breakdown halts the line."
            ),
            keyFramework = "Volume–Variety Process Matrix"
        ),
        SyllabusTopic(
            id = "ops_3d_printing",
            part = M3Part.PART_A,
            title = "3D printing",
            subtitle = "Additive manufacturing applications & strategic trade-offs",
            difficulty = TopicDifficulty.CORE,
            definition = "3D printing (additive manufacturing) builds three-dimensional parts layer-by-layer from digital CAD files, rather than subtracting material via cutting or milling.",
            keyConcepts = listOf(
                "Advantages: rapid prototyping, mass customization, complex internal geometries, lower material scrap, decentralized on-demand spare parts.",
                "Limitations: slower unit cycle times at massive scale, material constraints, surface finish post-processing, IP/licensing risks."
            ),
            examples = listOf(
                "Aerospace brackets printed lighter with internal lattice structures; dental aligners customized per patient."
            ),
            revisionNotes = listOf(
                "Additive = builds layer-by-layer; Subtractive = CNC machining/milling.",
                "Reduces inventory holding of slow-moving spare parts via digital warehousing."
            ),
            keyFramework = "Additive vs. Subtractive Manufacturing"
        ),
        SyllabusTopic(
            id = "ops_plant_maintenance",
            part = M3Part.PART_A,
            title = "Plant maintenance",
            subtitle = "Breakdown, preventive, predictive & reliability strategies",
            difficulty = TopicDifficulty.CORE,
            definition = "Plant maintenance encompasses all technical and managerial actions to retain or restore machinery and facilities in a state where they can perform required functions safely and reliably.",
            keyConcepts = listOf(
                "Breakdown (Reactive/Run-to-failure) Maintenance: fix only after failure; low upfront cost, high unplanned downtime risk.",
                "Preventive Maintenance (PM): scheduled inspections/replacements based on time or usage hours.",
                "Predictive / Condition-Based Maintenance (CBM): IoT vibration, thermal, oil sensors detect early wear before failure.",
                "Reliability-Centered Maintenance (RCM): prioritizes maintenance by failure criticality."
            ),
            examples = listOf(
                "Vibration sensors on turbine bearings predicting wear 3 weeks before breakdown."
            ),
            revisionNotes = listOf(
                "Total maintenance cost = Cost of preventive maintenance + Cost of breakdowns.",
                "IoT + AI elevates CBM to real-time Predictive Maintenance."
            ),
            keyFramework = "Reactive -> Preventive -> Predictive Maintenance"
        ),
        SyllabusTopic(
            id = "ops_resource_transformation",
            part = M3Part.PART_A,
            title = "Resource transformation",
            subtitle = "Input–Transformation–Output model",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Operations management functions as a transformation system converting transformed resources (materials, information, customers) using transforming resources (facilities, staff) into goods and services.",
            keyConcepts = listOf(
                "Transformed Resources: Materials (physical state/location), Information (format/possession), Customers (physical/psychological state).",
                "Transforming Resources: Facilities (buildings, plant, tech) & Staff (skills, knowledge).",
                "Feedback loop monitors outputs against quality, cost, and delivery standards."
            ),
            examples = listOf(
                "Hospital transforms patients (customers) using doctors & MRI scanners (transforming resources) into healthier patients."
            ),
            revisionNotes = listOf(
                "Know the difference: Transformed (changed in process) vs. Transforming (agents of change)."
            ),
            keyFramework = "Input -> Transformation -> Output Model"
        ),
        SyllabusTopic(
            id = "ops_mrp",
            part = M3Part.PART_A,
            title = "MRP (Material Requirements Planning)",
            subtitle = "Time-phased dependent demand & BOM explosion",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "MRP is a computer-based inventory and production scheduling system that explodes the Master Production Schedule (MPS) using the Bill of Materials (BOM) and Inventory Records to determine exact component quantities and order release dates.",
            keyConcepts = listOf(
                "Three Key Inputs: Master Production Schedule (MPS), Bill of Materials (BOM), Inventory Status File.",
                "Independent Demand (finished goods, forecasted) vs. Dependent Demand (components calculated via BOM).",
                "Gross requirements - Scheduled receipts - On-hand = Net requirements; offset by lead time.",
                "Outputs: Planned order releases, rescheduling notices, exception reports."
            ),
            examples = listOf(
                "Scheduling 200 bicycles in Week 5 triggers MRP to order 400 tires in Week 3 given a 2-week supplier lead time."
            ),
            revisionNotes = listOf(
                "Memorize the 3 MRP inputs: MPS + BOM + Inventory Records.",
                "MRP prevents stock-outs while minimizing excess component inventory."
            ),
            keyFramework = "MPS + BOM + Inventory -> MRP"
        ),
        SyllabusTopic(
            id = "ops_mrp_ii",
            part = M3Part.PART_A,
            title = "MRP II (Manufacturing Resource Planning)",
            subtitle = "Closed-loop capacity, labor & financial integration",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "MRP II expands MRP into an integrated closed-loop manufacturing planning system incorporating Capacity Requirements Planning (CRP), shop-floor control, machine/labor hours, and financial/costing simulations.",
            keyConcepts = listOf(
                "Closed-loop feedback checks whether capacity exists before locking the MPS.",
                "Integrates engineering, manufacturing, purchasing, and finance.",
                "Supports 'What-If' simulation of schedule and cash-flow impacts."
            ),
            examples = listOf(
                "Testing whether a 20% rush order fits CNC machine capacity and overtime budgets before confirming customer delivery."
            ),
            revisionNotes = listOf(
                "MRP = materials only; MRP II = materials + machine capacity + labor + financial planning."
            ),
            keyFramework = "Closed-Loop MRP II"
        ),
        SyllabusTopic(
            id = "ops_erp",
            part = M3Part.PART_A,
            title = "ERP (Enterprise Resource Planning)",
            subtitle = "Single-database enterprise integration",
            difficulty = TopicDifficulty.CORE,
            definition = "ERP integrates all core organizational functions—Finance, Supply Chain, Manufacturing, HRM, CRM, and Procurement—onto a unified real-time database and software architecture.",
            keyConcepts = listOf(
                "Single source of truth eliminates data silos and duplicate entry.",
                "Modular architecture (FI/CO, MM, PP, SD, HCM).",
                "Implementation challenges: high cost, business process reengineering (BPR) vs customization, change resistance."
            ),
            examples = listOf(
                "When sales logs an order in SAP/Oracle, inventory reserves stock, MRP updates production, and finance posts receivables automatically."
            ),
            revisionNotes = listOf(
                "Evolution path: MRP -> MRP II -> ERP.",
                "Prefer adopting standard ERP best-practice workflows over heavy custom coding."
            ),
            keyFramework = "Unified Enterprise Data Core"
        ),
        SyllabusTopic(
            id = "ops_opt",
            part = M3Part.PART_A,
            title = "Optimized Production Technology (OPT)",
            subtitle = "Goldratt's Theory of Constraints & Drum-Buffer-Rope",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "OPT (rooted in Eliyahu Goldratt's Theory of Constraints) maximizes factory throughput by identifying and scheduling around system bottlenecks rather than balancing capacities equally across all workstations.",
            keyConcepts = listOf(
                "An hour lost at a bottleneck is an hour lost for the entire system; an hour saved at a non-bottleneck is a mirage.",
                "Balance flow, not capacity; utilization != activation (producing unneeded WIP at non-bottlenecks increases inventory).",
                "Drum-Buffer-Rope (DBR): Bottleneck sets the pace (Drum), safety stock protects the bottleneck (Buffer), and material release is tied to bottleneck consumption (Rope).",
                "Transfer batch need not equal process batch."
            ),
            examples = listOf(
                "A paint curing oven takes 40 min while cutting takes 10 min: schedule the entire factory strictly to the curing oven's pace."
            ),
            revisionNotes = listOf(
                "Throughput = Sales revenue minus totally variable material cost.",
                "Never starve a bottleneck and never flood WIP ahead of non-bottlenecks."
            ),
            keyFramework = "Theory of Constraints & Drum-Buffer-Rope"
        ),
        SyllabusTopic(
            id = "ops_capacity_management",
            part = M3Part.PART_A,
            title = "Capacity management",
            subtitle = "Design vs effective capacity, utilization & demand balancing",
            difficulty = TopicDifficulty.CORE,
            definition = "Capacity management balances the maximum sustainable output capability of an operation against fluctuating customer demand across short, medium, and long-term horizons.",
            keyConcepts = listOf(
                "Design Capacity (theoretical max) vs. Effective Capacity (minus planned maintenance/setups) vs. Actual Output.",
                "Utilization = Actual Output / Design Capacity; Efficiency = Actual Output / Effective Capacity.",
                "Strategies: Level capacity (build inventory/backlogs), Chase demand (flex shifts/temps), Demand management (off-peak pricing)."
            ),
            examples = listOf(
                "Airlines using dynamic pricing (yield management) to smooth peak vs. off-peak seat demand."
            ),
            revisionNotes = listOf(
                "Utilization denominator = Design Capacity; Efficiency denominator = Effective Capacity.",
                "Services cannot inventory output, so queuing & demand-shaping are vital."
            ),
            keyFramework = "Level vs. Chase vs. Yield Management"
        ),
        SyllabusTopic(
            id = "ops_tqm",
            part = M3Part.PART_A,
            title = "Total Quality Management (TQM)",
            subtitle = "Organization-wide quality culture & Cost of Quality",
            difficulty = TopicDifficulty.CORE,
            definition = "TQM is a holistic management philosophy uniting every employee, supplier, and process around continuous improvement to exceed customer expectations and eliminate defects.",
            keyConcepts = listOf(
                "Core Pillars: Customer focus, total employee involvement, process-centered, continuous improvement, fact-based decisions.",
                "Cost of Quality (PAF Model): Prevention costs + Appraisal costs (conformance) vs. Internal Failure + External Failure costs (non-conformance).",
                "Gurus: Deming (PDCA & 14 points), Juran (fitness for use), Crosby (zero defects / quality is free)."
            ),
            examples = listOf(
                "Spending PKR 1M on operator training & poka-yoke (Prevention) eliminates PKR 8M in warranty recalls (External Failure)."
            ),
            revisionNotes = listOf(
                "External failure is the costliest quality failure (lost reputation + warranty + recalls).",
                "Prevention investments yield the highest ROI."
            ),
            keyFramework = "PAF Cost of Quality & Deming PDCA"
        ),
        SyllabusTopic(
            id = "ops_kaizen",
            part = M3Part.PART_A,
            title = "Kaizen",
            subtitle = "Continuous incremental improvement & Gemba focus",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Kaizen ('change for the better') is the Japanese philosophy of ongoing, incremental improvements involving everyone from senior executives to shop-floor workers without massive capital outlay.",
            keyConcepts = listOf(
                "Small daily improvements compound over time (contrasts with radical BPR/Kaiaku).",
                "Gemba (go to the actual workplace), 5 Whys root-cause inquiry, Quality Control Circles (QCC).",
                "Employee empowerment and suggestion schemes."
            ),
            examples = listOf(
                "Relocating a wrench rack 2 meters closer to a workstation after operator feedback, saving 15 minutes per shift."
            ),
            revisionNotes = listOf(
                "Kaizen = continuous small steps by everyone; BPR = radical top-down redesign."
            ),
            keyFramework = "Gemba + PDCA + 5 Whys"
        ),
        SyllabusTopic(
            id = "ops_lean_production",
            part = M3Part.PART_A,
            title = "Lean Production",
            subtitle = "Eliminating 7 Muda wastes, JIT & Kanban pull",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Lean production maximizes customer value while ruthlessly eliminating non-value-adding waste (Muda), unevenness (Mura), and overburden (Muri) using Just-in-Time (JIT) pull systems.",
            keyConcepts = listOf(
                "7 Wastes (TIMWOOD): Transport, Inventory, Motion, Waiting, Over-production, Over-processing, Defects (+ unused talent).",
                "JIT & Kanban: downstream pull signals trigger upstream replenishment in small batches.",
                "5S Housekeeping: Sort (Seiri), Set in order (Seiton), Shine (Seiso), Standardize (Seiketsu), Sustain (Shitsuke)."
            ),
            examples = listOf(
                "Toyota assembly line using two-bin Kanban cards so parts arrive hours before installation instead of sitting in warehouses."
            ),
            revisionNotes = listOf(
                "Over-production is often called the worst waste because it hides & creates all other wastes.",
                "JIT requires highly reliable suppliers and low setup times (SMED)."
            ),
            keyFramework = "TIMWOOD 7 Wastes + 5S + Kanban Pull"
        ),
        SyllabusTopic(
            id = "ops_tpm",
            part = M3Part.PART_A,
            title = "Total Productive Maintenance (TPM)",
            subtitle = "Autonomous maintenance & OEE maximization",
            difficulty = TopicDifficulty.CORE,
            definition = "TPM engages operators and maintenance technicians jointly to eliminate breakdowns, defects, and accidents, maximizing Overall Equipment Effectiveness (OEE).",
            keyConcepts = listOf(
                "OEE = Availability x Performance x Quality.",
                "Autonomous Maintenance: frontline operators clean, inspect, lubricate, and tighten daily.",
                "Six Big Losses: Equipment failure, setup/adjustments, idling/minor stops, reduced speed, process defects, startup losses."
            ),
            examples = listOf(
                "Machine operators completing a 10-minute morning 5S & lubrication checklist before starting a shift."
            ),
            revisionNotes = listOf(
                "World-class OEE benchmark is typically ~85%.",
                "TPM breaks the 'I operate, you fix' silo."
            ),
            keyFramework = "OEE = Availability × Performance × Quality"
        ),
        SyllabusTopic(
            id = "ops_tqmex_model",
            part = M3Part.PART_A,
            title = "TQMEX model",
            subtitle = "6-step integrated excellence roadmap",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "The TQMEX model integrates six foundational operational excellence initiatives into a structured transformation sequence leading to Total Quality Management.",
            keyConcepts = listOf(
                "Six TQMEX Steps: 1. 5-S (workplace organization), 2. BPR (Business Process Re-engineering), 3. QCC (Quality Control Circles), 4. ISO 9000 (standardized quality management system), 5. TPM (equipment reliability), 6. TQM (company-wide continuous excellence).",
                "Note: Combines foundational housekeeping, radical process redesign, worker participation, formal standards, and asset care."
            ),
            examples = listOf(
                "A manufacturing plant establishing 5S and QCC teams prior to ISO 9001 certification and TPM rollout."
            ),
            revisionNotes = listOf(
                "Sequence: 5-S -> BPR -> QCC -> ISO 9000 -> TPM -> TQM.",
                "Remember BPR redesigns broken core processes radically, while QCC engages teams incrementally."
            ),
            keyFramework = "TQMEX 6-Step Sequence"
        ),
        SyllabusTopic(
            id = "ops_ai_quality_control",
            part = M3Part.PART_A,
            title = "AI-driven quality control",
            subtitle = "Computer vision inspection & automated defect analytics",
            difficulty = TopicDifficulty.CORE,
            definition = "AI-driven quality control uses machine learning, computer vision, and real-time sensor analytics to detect microscopic defects, predict process drift, and automate sorting at production speed.",
            keyConcepts = listOf(
                "Computer vision detects surface scratches, solder defects, and label errors faster and more consistently than manual checks.",
                "Statistical Process Control (SPC) enhanced with anomaly-detection models.",
                "Challenges: training data quality, false positives, model drift, integration with legacy PLC lines."
            ),
            examples = listOf(
                "High-speed cameras on a pharmaceutical blister line rejecting cracked tablets automatically via ML classification."
            ),
            revisionNotes = listOf(
                "Reduces external failure costs and human visual fatigue.",
                "Requires clean labeled defect datasets and human-in-the-loop oversight."
            ),
            keyFramework = "Computer Vision + Real-Time SPC"
        ),
        SyllabusTopic(
            id = "ops_service_quality",
            part = M3Part.PART_A,
            title = "Service quality",
            subtitle = "IHIP characteristics, SERVQUAL (RATER) & Gaps Model",
            difficulty = TopicDifficulty.CORE,
            definition = "Service quality measures how consistently delivered service meets or exceeds customer expectations across intangible experiences, evaluated through the SERVQUAL framework.",
            keyConcepts = listOf(
                "Service Traits (IHIP): Intangibility, Heterogeneity (variability), Inseparability (simultaneous production/consumption), Perishability (cannot store).",
                "SERVQUAL 5 Dimensions (RATER): Reliability, Assurance, Tangibles, Empathy, Responsiveness.",
                "5 Gaps Model: Gap 5 (Expected vs. Perceived service) driven by Gaps 1–4."
            ),
            examples = listOf(
                "A bank reducing branch wait times (Responsiveness) and training tellers in polite personalized care (Empathy & Assurance)."
            ),
            revisionNotes = listOf(
                "Mnemonic RATER = Reliability, Assurance, Tangibles, Empathy, Responsiveness.",
                "Reliability is consistently rated the single most important SERVQUAL dimension."
            ),
            keyFramework = "IHIP + SERVQUAL (RATER) + 5 Gaps"
        ),
        SyllabusTopic(
            id = "ops_iso_standards",
            part = M3Part.PART_A,
            title = "ISO standards, including ISO 56005",
            subtitle = "ISO 9001, 14001, 45001 & ISO 56005 IP management",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "International Organization for Standardization (ISO) frameworks establish globally recognized benchmarks for quality, environmental sustainability, safety, and innovation/intellectual property management—including ISO 56005.",
            keyConcepts = listOf(
                "ISO 9001: Quality Management System (QMS) built on process approach and customer satisfaction.",
                "ISO 14001: Environmental Management System; ISO 45001: Occupational Health & Safety; ISO 27001: Information Security.",
                "ISO 56005: Innovation Management — Tools and methods for Intellectual Property (IP) management (aligns IP strategy with innovation lifecycle, freedom-to-operate, patenting/trade secrets)."
            ),
            examples = listOf(
                "An R&D-driven med-tech startup adopting ISO 56005 to govern patent filings, confidentiality, and licensing before product launch."
            ),
            revisionNotes = listOf(
                "ISO 56005 is specifically about Intellectual Property Management within Innovation Management!",
                "ISO 9001 = Quality; ISO 14001 = Environment; ISO 45001 = OH&S; ISO 56005 = IP Management."
            ),
            keyFramework = "ISO Family + ISO 56005 (IP Management)"
        )
    )

    val partBTopics: List<SyllabusTopic> = listOf(
        SyllabusTopic(
            id = "hr_human_capital_theories",
            part = M3Part.PART_B,
            title = "Human capital theories",
            subtitle = "Knowledge, skills, ROI on people & Resource-Based View",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Human capital theory treats employees' education, skills, health, and tacit knowledge as productive economic investments rather than mere payroll expenses.",
            keyConcepts = listOf(
                "Becker & Schultz: investments in training and education boost productivity and lifetime earnings.",
                "VRIO / Resource-Based View: Valuable, Rare, Inimitable, Organized talent creates durable competitive advantage.",
                "General vs. Firm-specific human capital (affects retention strategy)."
            ),
            examples = listOf(
                "Sponsoring CIMA/ICMA certifications to elevate management accounting capabilities across finance teams."
            ),
            revisionNotes = listOf(
                "Physical assets depreciate; human capital appreciates through learning and experience.",
                "General skills are portable (risk of poaching); firm-specific skills require internal retention."
            ),
            keyFramework = "Human Capital + VRIO Framework"
        ),
        SyllabusTopic(
            id = "hr_employee_motivation",
            part = M3Part.PART_B,
            title = "Employee motivation",
            subtitle = "Maslow, Herzberg, McGregor, Vroom & Adams Equity",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Employee motivation theories explain the internal and external psychological forces that initiate, direct, and sustain work effort toward organizational goals.",
            keyConcepts = listOf(
                "Maslow's Hierarchy: Physiological -> Safety -> Social -> Esteem -> Self-Actualization.",
                "Herzberg's Two-Factor Theory: Hygiene factors (pay, conditions, policies prevent dissatisfaction) vs. Motivators (achievement, recognition, growth drive satisfaction).",
                "McGregor Theory X (control/extrinsic) vs. Theory Y (trust/intrinsic); Vroom Expectancy (Valence x Expectancy x Instrumentality); Adams Equity Theory (input/output fairness)."
            ),
            examples = listOf(
                "Raising salary fixes Hygiene dissatisfaction briefly, but meaningful autonomy and promotion recognition serve as long-term Motivators."
            ),
            revisionNotes = listOf(
                "Herzberg: Opposite of Satisfaction is No Satisfaction; Opposite of Dissatisfaction is No Dissatisfaction.",
                "Vroom: Motivation = E x I x V (if any factor is 0, motivation drops to 0)."
            ),
            keyFramework = "Maslow · Herzberg · Vroom · Equity"
        ),
        SyllabusTopic(
            id = "hr_remote_hybrid_policies",
            part = M3Part.PART_B,
            title = "Remote and hybrid work policies",
            subtitle = "Flexible work arrangements, governance & productivity",
            difficulty = TopicDifficulty.CORE,
            definition = "Remote and hybrid work policies establish eligibility, communication protocols, cybersecurity rules, output-based performance norms, and ergonomic/well-being standards for distributed teams.",
            keyConcepts = listOf(
                "Benefits: wider talent pool, lower commute stress, reduced office overhead, higher retention.",
                "Risks: proximity bias, onboarding friction, blurred work-life boundaries, cybersecurity exposure.",
                "Success requires outcome-based KPIs rather than 'presenteeism'."
            ),
            examples = listOf(
                "3-2 hybrid policy with core collaboration hours (10 AM–3 PM), VPN enforcement, and home-office stipend."
            ),
            revisionNotes = listOf(
                "Guard against 'proximity bias' in appraisals when comparing in-office vs remote staff.",
                "Balance synchronous bonding days with asynchronous deep-work blocks."
            ),
            keyFramework = "Output-Based Hybrid Governance"
        ),
        SyllabusTopic(
            id = "hr_codes_of_conduct",
            part = M3Part.PART_B,
            title = "Codes of conduct",
            subtitle = "Workplace ethics, harassment, conflict of interest & discipline",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "A Code of Conduct formalizes expected standards of professional behavior, ethics, confidentiality, anti-harassment, conflict-of-interest disclosure, and disciplinary procedures.",
            keyConcepts = listOf(
                "Applies uniformly across leadership and staff.",
                "Covers integrity, data privacy, anti-discrimination, gift policies, and grievance channels.",
                "Effective only when backed by regular training, leadership role-modeling, and fair enforcement."
            ),
            examples = listOf(
                "Mandatory annual conflict-of-interest declaration for procurement officers and board members."
            ),
            revisionNotes = listOf(
                "A code sitting unread in a PDF fails; it needs active onboarding and safe reporting channels."
            ),
            keyFramework = "Standards -> Training -> Fair Enforcement"
        ),
        SyllabusTopic(
            id = "hr_algorithmic_recruitment_bias",
            part = M3Part.PART_B,
            title = "Algorithmic recruitment bias",
            subtitle = "AI screening risks, historical bias & human-in-the-loop audits",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Algorithmic recruitment bias occurs when automated resume screeners, video analyzers, or ranking models systematically disadvantage protected groups due to skewed historical training data or proxy variables.",
            keyConcepts = listOf(
                "Root causes: biased past hiring data, proxy attributes (zip codes, college names, employment gaps), opaque black-box scoring.",
                "Mitigations: diverse training datasets, blind resume screening, regular disparate-impact audits, explainable AI (XAI), human-in-the-loop final decisions."
            ),
            examples = listOf(
                "An AI resume model trained on 10 years of male-dominated engineering hires penalizing resumes mentioning women's colleges."
            ),
            revisionNotes = listOf(
                "AI reflects patterns in historical data—if past hiring was biased, automation scales that bias!",
                "Always combine algorithmic shortlisting with structured human oversight and fairness audits."
            ),
            keyFramework = "Bias Audit + XAI + Human-in-the-Loop"
        ),
        SyllabusTopic(
            id = "hr_planning",
            part = M3Part.PART_B,
            title = "HR planning",
            subtitle = "Demand/supply forecasting, gap analysis & succession",
            difficulty = TopicDifficulty.CORE,
            definition = "Human Resource Planning (HRP) forecasts future workforce demand and internal/external labor supply to ensure the right number of people with the right skills are in the right roles at the right time.",
            keyConcepts = listOf(
                "Steps: Strategic analysis -> Demand forecasting -> Supply forecasting (Markov/replacement charts) -> Gap analysis -> Action plans.",
                "Shortage solutions: recruiting, upskilling, overtime, outsourcing.",
                "Surplus solutions: hiring freeze, retraining, voluntary retirement, decruitment."
            ),
            examples = listOf(
                "Planning 18 months ahead for 40 new data analysts ahead of an ERP/analytics rollout."
            ),
            revisionNotes = listOf(
                "HRP links corporate strategy directly to talent pipelines and succession charts."
            ),
            keyFramework = "Demand Forecast – Supply Forecast = Gap"
        ),
        SyllabusTopic(
            id = "hr_selection",
            part = M3Part.PART_B,
            title = "Selection",
            subtitle = "Screening, interviews, assessment centers & validity",
            difficulty = TopicDifficulty.CORE,
            definition = "Selection is the systematic evaluation of applicants through structured interviews, psychometric tests, work samples, and background checks to appoint candidates best suited to job and organizational fit.",
            keyConcepts = listOf(
                "Reliability (consistency of test) vs. Validity (predictive accuracy for job performance).",
                "Methods: structured/behavioral interviews, assessment centers, cognitive/aptitude tests, work samples, reference checks.",
                "Interview biases: halo effect, stereotyping, contrast effect, first-impression bias."
            ),
            examples = listOf(
                "Using standardized behavioral scorecards ('Tell me about a time...') + a practical case exercise to minimize interviewer bias."
            ),
            revisionNotes = listOf(
                "Work samples & structured interviews show much higher predictive validity than unstructured chats.",
                "Past-paper favorite: limitations of interviews include halo effect & subjective bias."
            ),
            keyFramework = "Reliability · Validity · Assessment Centers"
        ),
        SyllabusTopic(
            id = "hr_appraisals",
            part = M3Part.PART_B,
            title = "Appraisals",
            subtitle = "360-degree feedback, BARS, MBO & rater bias",
            difficulty = TopicDifficulty.CORE,
            definition = "Performance appraisals formally assess an employee's job results and competencies against agreed objectives to guide development, feedback, promotions, and pay.",
            keyConcepts = listOf(
                "Methods: MBO (Management by Objectives), BARS (Behaviorally Anchored Rating Scales), 360-degree feedback, forced ranking.",
                "Rater errors: leniency, severity, central tendency, recency bias, halo/horn effect.",
                "Continuous coaching vs. once-a-year retrospective review."
            ),
            examples = listOf(
                "Combining quarterly MBO KPI check-ins with 360-degree peer/subordinate behavioral feedback."
            ),
            revisionNotes = listOf(
                "360-degree gathers input from supervisor, peers, subordinates, self, and sometimes customers.",
                "Recency bias overweights the last 4 weeks instead of the full year."
            ),
            keyFramework = "MBO · BARS · 360° Feedback"
        ),
        SyllabusTopic(
            id = "hr_compensation",
            part = M3Part.PART_B,
            title = "Compensation",
            subtitle = "Job evaluation, total rewards, equity & variable pay",
            difficulty = TopicDifficulty.CORE,
            definition = "Compensation management designs equitable base pay, incentives, benefits, and non-monetary rewards to attract, motivate, and retain talent within financial constraints.",
            keyConcepts = listOf(
                "Internal equity (job evaluation/point-factor) vs. External competitiveness (market salary surveys).",
                "Fixed base pay vs. Variable pay (bonuses, commissions, profit-sharing, ESOPs).",
                "Total Rewards: base + variable + benefits + work-life + recognition + career growth."
            ),
            examples = listOf(
                "Linking 20% of managerial compensation to balanced scorecard KPIs plus long-term stock options."
            ),
            revisionNotes = listOf(
                "Over-reliance on short-term bonuses can trigger unethical short-termism.",
                "Job evaluation evaluates the ROLE, whereas appraisals evaluate the PERSON."
            ),
            keyFramework = "Internal Equity + External Pay Competitiveness"
        ),
        SyllabusTopic(
            id = "hr_training",
            part = M3Part.PART_B,
            title = "Training",
            subtitle = "ADDIE, Kolb learning cycle & Kirkpatrick evaluation",
            difficulty = TopicDifficulty.CORE,
            definition = "Training and Development builds job-specific capabilities (Training) and long-term career potential (Development) through systematic needs analysis, delivery, and ROI evaluation.",
            keyConcepts = listOf(
                "ADDIE: Analyze needs (Org/Task/Person) -> Design -> Develop -> Implement -> Evaluate.",
                "Kolb's Experiential Learning Cycle: Concrete Experience -> Reflective Observation -> Abstract Conceptualization -> Active Experimentation.",
                "Kirkpatrick 4 Levels: Reaction -> Learning -> Behavior (on-the-job transfer) -> Results (business KPI impact)."
            ),
            examples = listOf(
                "Measuring sales training not just by post-class smiles (Level 1) but by CRM call quality (Level 3) and conversion lift (Level 4)."
            ),
            revisionNotes = listOf(
                "Memorize Kirkpatrick's 4 levels in order: Reaction, Learning, Behavior, Results!",
                "Needs analysis checks Organization, Task, and Individual gaps before buying courses."
            ),
            keyFramework = "ADDIE + Kolb + Kirkpatrick 4 Levels"
        ),
        SyllabusTopic(
            id = "hr_decruitment",
            part = M3Part.PART_B,
            title = "Decruitment",
            subtitle = "Workforce downsizing, attrition, redeployment & outplacement",
            difficulty = TopicDifficulty.CORE,
            definition = "Decruitment is the structured, humane reduction or rebalancing of an organization's workforce when headcount exceeds strategic or financial requirements.",
            keyConcepts = listOf(
                "Options (least to most disruptive): attrition/hiring freeze, reduced workweeks, early retirement incentives, redeployment/reskilling, layoffs/firing.",
                "Legal notice, fair severance, survivor syndrome management, and outplacement career support."
            ),
            examples = listOf(
                "Reskilling redundant branch tellers into digital customer-success specialists instead of immediate layoffs."
            ),
            revisionNotes = listOf(
                "Survivor syndrome causes remaining staff anxiety and drops productivity if communication is poor.",
                "Attrition & voluntary early retirement preserve morale better than sudden layoffs."
            ),
            keyFramework = "Humane Decruitment Spectrum"
        ),
        SyllabusTopic(
            id = "hr_ex_platforms",
            part = M3Part.PART_B,
            title = "Employee Experience (EX) platforms",
            subtitle = "Unified digital workplace engagement & journey analytics",
            difficulty = TopicDifficulty.CORE,
            definition = "Employee Experience (EX) platforms integrate pulse surveys, onboarding journeys, learning portals, peer recognition, and HR self-service into a seamless digital workplace ecosystem.",
            keyConcepts = listOf(
                "Shifts HR from isolated transactions to holistic employee lifecycle design (hire-to-retire).",
                "Real-time pulse listening, sentiment analysis, personalized learning, recognition feeds.",
                "Drives engagement, lower turnover, and higher customer satisfaction."
            ),
            examples = listOf(
                "Microsoft Viva or Workday Peakon surfacing early engagement dips in a regional sales unit."
            ),
            revisionNotes = listOf(
                "EX mirrors CX (Customer Experience): satisfied employees deliver better customer experiences."
            ),
            keyFramework = "Hire-to-Retire Digital EX Loop"
        ),
        SyllabusTopic(
            id = "hr_mental_health_analytics",
            part = M3Part.PART_B,
            title = "Mental health analytics",
            subtitle = "Burnout early-warning signals & ethical privacy safeguards",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Mental health analytics uses aggregated, anonymized workforce indicators—overtime patterns, leave trends, pulse well-being scores, and EAP utilization—to proactively prevent burnout while protecting individual medical privacy.",
            keyConcepts = listOf(
                "Early warning signs: sustained excessive overtime, skipped PTO, sudden absenteeism spikes, declining engagement.",
                "Ethical Imperative: STRICT anonymization, opt-in consent, aggregate team-level insights only (never weaponized in appraisals or discipline).",
                "Proactive interventions: workload rebalancing, mental health days, manager empathy training."
            ),
            examples = listOf(
                "An anonymized dashboard alerts HR that a project team averaged 62-hour weeks for a month, prompting temporary contractor support."
            ),
            revisionNotes = listOf(
                "Privacy & trust are paramount: never track individual keystrokes or expose personal clinical data to managers."
            ),
            keyFramework = "Aggregate Well-Being Signals + Privacy Shield"
        )
    )
}
