package com.example.data

import com.example.model.M3Part
import com.example.model.McqQuestion
import com.example.model.QuestionDifficulty

internal object QuestionBankPartCD {
    private const val STUDY_TEXT_LABEL = "Study Text Practice (ICMA M3)"
    private const val PAST_PAPER_LABEL = "Verified Past-Paper Practice (ICMA ML-1)"

    val partCMcqs: List<McqQuestion> = listOf(
        McqQuestion(
            id = "q_c1", part = M3Part.PART_C, topicId = "mkt_marketing_philosophies",
            topicTitle = "Marketing philosophies", difficulty = QuestionDifficulty.MEDIUM,
            question = "Theodore Levitt's concept of 'Marketing Myopia' occurs when management:",
            options = listOf(
                "Focuses so narrowly on improving current product features that it misses evolving underlying customer needs",
                "Spends too much budget on customer research",
                "Uses integrated marketing communications",
                "Lowers product prices during a recession"
            ),
            correctIndex = 0,
            explanation = "The Product Concept often breeds Marketing Myopia—falling in love with a product rather than the customer problem it solves.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c2", part = M3Part.PART_C, topicId = "mkt_marketing_philosophies",
            topicTitle = "Marketing philosophies", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "Which marketing orientation relies heavily on aggressive promotion to persuade buyers to take whatever the company already produces, focusing on short-term sales volume?",
            options = listOf("Marketing Concept", "Selling Orientation", "Societal Marketing Concept", "Internal Marketing"),
            correctIndex = 1,
            explanation = "Selling orientation adopts an inside-out view centered on transactional sales volume rather than long-run customer satisfaction.",
            sourceLabel = PAST_PAPER_LABEL,
            isPastPaper = true
        ),
        McqQuestion(
            id = "q_c3", part = M3Part.PART_C, topicId = "mkt_corporate_vs_marketing",
            topicTitle = "Corporate vs. marketing strategy", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "In the Ansoff Growth Matrix, launching an existing product line into a completely new geographic or demographic segment is classified as:",
            options = listOf("Market Penetration", "Product Development", "Market Development", "Diversification"),
            correctIndex = 2,
            explanation = "Existing Product + New Market = Market Development (whereas New Product + New Market = Diversification).",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c4", part = M3Part.PART_C, topicId = "mkt_corporate_vs_marketing",
            topicTitle = "Corporate vs. marketing strategy", difficulty = QuestionDifficulty.MEDIUM,
            question = "In the BCG Growth-Share Matrix, Strategic Business Units (SBUs) with high relative market share in a mature low-growth market that generate surplus cash are:",
            options = listOf("Stars", "Question Marks", "Cash Cows", "Dogs"),
            correctIndex = 2,
            explanation = "Cash Cows hold high market share in low-growth markets and fund Stars and selected Question Marks.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c5", part = M3Part.PART_C, topicId = "mkt_imc",
            topicTitle = "Integrated marketing communications (IMC)", difficulty = QuestionDifficulty.MEDIUM,
            question = "A 'Pull' promotional strategy primarily directs marketing efforts toward:",
            options = listOf(
                "Wholesalers and retail buyers using trade discounts",
                "End consumers via advertising and digital campaigns so they demand the product from retailers",
                "Factory maintenance engineers",
                "Government tax inspectors"
            ),
            correctIndex = 1,
            explanation = "Pull strategies stimulate end-consumer demand, pulling goods through the distribution channel.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c6", part = M3Part.PART_C, topicId = "mkt_social_marketing",
            topicTitle = "Social marketing", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "How is 'Social Marketing' accurately defined in ICMA M3 Marketing Management?",
            options = listOf(
                "Posting promotional reels exclusively on Instagram and TikTok",
                "Applying marketing principles to influence voluntary behavior change for public health, safety, or social good",
                "Hosting corporate holiday parties",
                "Selling luxury items via celebrity endorsements"
            ),
            correctIndex = 1,
            explanation = "Social marketing targets positive societal behavioral change (e.g., road safety, sanitation), unlike commercial social media marketing.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c7", part = M3Part.PART_C, topicId = "mkt_npo_marketing",
            topicTitle = "NPO marketing", difficulty = QuestionDifficulty.MEDIUM,
            question = "What makes Non-Profit Organization (NPO) marketing structurally distinct from commercial marketing?",
            options = listOf(
                "NPOs have zero budgets and cannot advertise",
                "NPOs must serve two distinct audiences simultaneously: resource providers (donors/grants) and service beneficiaries",
                "NPOs ignore brand reputation",
                "NPOs maximize dividend payouts"
            ),
            correctIndex = 1,
            explanation = "NPO marketing balances dual constituencies: attracting funds/volunteers while delivering mission value to beneficiaries.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c8", part = M3Part.PART_C, topicId = "mkt_internal_marketing",
            topicTitle = "Internal marketing", difficulty = QuestionDifficulty.EASY,
            question = "Internal marketing focuses primarily on:",
            options = listOf(
                "Training, motivating, and aligning employees as internal customers to deliver the external brand promise",
                "Selling factory scrap to staff members",
                "Advertising strictly on intranet banners",
                "Cutting customer service staff"
            ),
            correctIndex = 0,
            explanation = "Internal marketing aligns frontline and support staff with brand values so customer experiences match external promises.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c9", part = M3Part.PART_C, topicId = "mkt_consumer_behavior",
            topicTitle = "Consumer behavior", difficulty = QuestionDifficulty.MEDIUM,
            question = "Post-purchase anxiety or second-guessing after buying an expensive item (such as a car or laptop) is known as:",
            options = listOf("Selective retention", "Cognitive dissonance", "Market segmentation", "Brand extension"),
            correctIndex = 1,
            explanation = "Cognitive dissonance (buyer's remorse) occurs when consumers doubt whether they chose the best alternative after purchase.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c10", part = M3Part.PART_C, topicId = "mkt_micro_influencers",
            topicTitle = "Micro-influencer partnerships", difficulty = QuestionDifficulty.MEDIUM,
            question = "Why do brands frequently achieve higher engagement ROI with micro-influencers (10k–100k followers) than with mega-celebrities?",
            options = listOf(
                "Micro-influencers enjoy higher niche audience trust, authentic interaction rates, and lower sponsorship fees",
                "Micro-influencers are exempt from advertising disclosure laws",
                "Micro-influencers reach 100 million global viewers per post",
                "Micro-influencers control retail shelf space"
            ),
            correctIndex = 0,
            explanation = "Niche credibility and close follower dialogue produce superior engagement per rupee spent.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c11", part = M3Part.PART_C, topicId = "mkt_brand_management",
            topicTitle = "Brand management", difficulty = QuestionDifficulty.DIFFICULT,
            question = "A conglomerate marketing Tide, Pampers, and Gillette under distinct standalone brand identities without a shared corporate prefix uses which brand architecture?",
            options = listOf("Branded House", "House of Brands", "Co-branding", "Generic white-labeling"),
            correctIndex = 1,
            explanation = "A House of Brands uses independent sub-brands to target separate segments and insulate reputation risk.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c12", part = M3Part.PART_C, topicId = "mkt_metaverse_virtual",
            topicTitle = "Metaverse and virtual branding", difficulty = QuestionDifficulty.MEDIUM,
            question = "Selling digital sneakers or skins directly to users' 3D personas inside persistent virtual worlds exemplifies:",
            options = listOf("Direct-to-Avatar (D2A) virtual branding", "Batch MRP explosion", "Zero-based budgeting", "Decruitment"),
            correctIndex = 0,
            explanation = "D2A commerce delivers virtual goods and immersive brand experiences directly inside metaverse environments.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c13", part = M3Part.PART_C, topicId = "mkt_web3_nft",
            topicTitle = "Web3 / NFT-based campaigns", difficulty = QuestionDifficulty.DIFFICULT,
            question = "What drives long-term effectiveness in Web3/NFT brand campaigns rather than short-lived hype?",
            options = listOf(
                "Speculative crypto volatility alone",
                "Verifiable utility such as token-gated loyalty perks, provenance authentication, and community access",
                "Hiding product pricing from buyers",
                "Avoiding customer support completely"
            ),
            correctIndex = 1,
            explanation = "Sustainable Web3 marketing pairs blockchain provenance or digital passes with tangible customer utility.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c14", part = M3Part.PART_C, topicId = "mkt_marketing_environment",
            topicTitle = "Marketing environment", difficulty = QuestionDifficulty.EASY,
            question = "Suppliers, marketing intermediaries, competitors, and customers belong to a firm's:",
            options = listOf("Macro-environment", "Micro-environment", "Internal ledger", "Technostructure"),
            correctIndex = 1,
            explanation = "Micro-environment comprises immediate task actors interacting closely with the company to serve customers.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_c15", part = M3Part.PART_C, topicId = "mkt_corporate_vs_marketing",
            topicTitle = "Corporate vs. marketing strategy", difficulty = QuestionDifficulty.MEDIUM,
            question = "In the extended 7Ps Services Marketing Mix, which three Ps are added to Product, Price, Place, and Promotion?",
            options = listOf(
                "Profit, Patents, and Politics",
                "People, Process, and Physical Evidence",
                "Procurement, Plant, and Payroll",
                "Prevention, Appraisal, and Failure"
            ),
            correctIndex = 1,
            explanation = "Services expand the 4Ps with People, Process, and Physical Evidence because of service intangibility and inseparability.",
            sourceLabel = STUDY_TEXT_LABEL
        )
    )

    val partDMcqs: List<McqQuestion> = listOf(
        McqQuestion(
            id = "q_d1", part = M3Part.PART_D, topicId = "perf_csfs_vs_kpis",
            topicTitle = "Critical Success Factors (CSFs) vs KPIs", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "Which statement accurately differentiates a Critical Success Factor (CSF) from a Key Performance Indicator (KPI)?",
            options = listOf(
                "A CSF is a vital strategic area where the organization must succeed; a KPI is the specific quantifiable metric used to track that CSF",
                "A KPI is qualitative while a CSF is always expressed in currency",
                "CSFs are only used in non-profit charities",
                "KPIs replace the need for any strategic goals"
            ),
            correctIndex = 0,
            explanation = "CSFs define 'what must go right', while KPIs provide the numeric gauges verifying whether it is going right.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d2", part = M3Part.PART_D, topicId = "perf_balanced_scorecard",
            topicTitle = "Balanced Scorecard", difficulty = QuestionDifficulty.EASY,
            question = "Which four perspectives form Kaplan and Norton's Balanced Scorecard?",
            options = listOf(
                "Production, Product, Selling, Societal",
                "Financial, Customer, Internal Business Processes, and Learning & Growth",
                "Political, Economic, Social, and Legal",
                "Plan, Do, Check, and Act"
            ),
            correctIndex = 1,
            explanation = "The Balanced Scorecard integrates Financial, Customer, Internal Business Processes, and Learning & Growth perspectives.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d3", part = M3Part.PART_D, topicId = "perf_balanced_scorecard",
            topicTitle = "Balanced Scorecard", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "Which is a recognized limitation or challenge of implementing the Balanced Scorecard?",
            options = listOf(
                "It eliminates financial ratios entirely",
                "Metric overload and potential conflicts/trade-offs between perspectives without an automatic single weighting",
                "It can only measure manufacturing scrap",
                "It requires Zero-Based Budgeting legally"
            ),
            correctIndex = 1,
            explanation = "ICMA past papers highlight information overload, high setup effort, and difficulty balancing conflicting metrics across the 4 perspectives.",
            sourceLabel = PAST_PAPER_LABEL,
            isPastPaper = true
        ),
        McqQuestion(
            id = "q_d4", part = M3Part.PART_D, topicId = "perf_control_systems",
            topicTitle = "Control systems", difficulty = QuestionDifficulty.MEDIUM,
            question = "Auditing raw material quality BEFORE production begins to stop defects from ever entering the line is an example of:",
            options = listOf("Feedback control", "Feedforward control", "Post-mortem variance control", "Concurrent budgeting"),
            correctIndex = 1,
            explanation = "Feedforward control anticipates and prevents deviations before inputs enter the transformation process.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d5", part = M3Part.PART_D, topicId = "perf_budgetary_control",
            topicTitle = "Budgetary control", difficulty = QuestionDifficulty.DIFFICULT,
            question = "Why should a production manager's variable overhead spending variance be evaluated against a FLEXIBLE budget rather than the original fixed budget?",
            options = listOf(
                "Because flexible budgets adjust allowed costs to the actual output volume achieved, isolating true efficiency from volume variance",
                "Because fixed budgets never include direct materials",
                "Because flexible budgets ignore actual output completely",
                "Because ZBB prohibits flexible budgets"
            ),
            correctIndex = 0,
            explanation = "Flexing the budget to actual activity volume allows fair like-for-like cost control comparison.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d6", part = M3Part.PART_D, topicId = "perf_budgetary_control",
            topicTitle = "Budgetary control", difficulty = QuestionDifficulty.MEDIUM,
            question = "What dysfunctional behavior occurs when managers deliberately underestimate revenue or inflate cost estimates during budgeting to hit targets easily?",
            options = listOf("Kaizen", "Budgetary slack (padding)", "Feedforward alignment", "Residual income optimization"),
            correctIndex = 1,
            explanation = "Budgetary slack distorts resource allocation and masks true operational potential.",
            sourceLabel = PAST_PAPER_LABEL,
            isPastPaper = true
        ),
        McqQuestion(
            id = "q_d7", part = M3Part.PART_D, topicId = "perf_financial_nonfinancial_tools",
            topicTitle = "Financial and non-financial tools", difficulty = QuestionDifficulty.EXAM_LEVEL,
            question = "A division currently earns 22% ROI while company cost of capital is 12%. Why might evaluating the manager solely on ROI cause goal-congruence failure on a new 16% ROI project?",
            options = listOf(
                "Because accepting the 16% project lowers the division's average ROI below 22%, even though it adds value above the 12% cost of capital",
                "Because 16% is below zero",
                "Because Residual Income rejects projects above cost of capital",
                "Because ROI excludes operating profit"
            ),
            correctIndex = 0,
            explanation = "ROI can discourage profitable divisions from taking projects yielding between cost of capital (12%) and current ROI (22%); Residual Income solves this.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d8", part = M3Part.PART_D, topicId = "perf_financial_nonfinancial_tools",
            topicTitle = "Financial and non-financial tools", difficulty = QuestionDifficulty.DIFFICULT,
            question = "In Fitzgerald and Moon's Building Block model for service businesses, 'Results' consist of Financial Performance and:",
            options = listOf("Quality of Service", "Competitiveness", "Resource Utilization", "Innovation"),
            correctIndex = 1,
            explanation = "Fitzgerald & Moon split dimensions into Results (Financial Performance & Competitiveness) and Determinants (Quality, Flexibility, Resource Utilization, Innovation).",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d9", part = M3Part.PART_D, topicId = "perf_realtime_iot_dashboards",
            topicTitle = "Real-time IoT dashboards", difficulty = QuestionDifficulty.MEDIUM,
            question = "How do real-time IoT dashboards strengthen management control compared with traditional monthly variance reports?",
            options = listOf(
                "They enable immediate concurrent control and anomaly alerts while operations are still running",
                "They eliminate all network security requirements",
                "They guarantee 100% customer satisfaction automatically",
                "They replace corporate strategy formulation"
            ),
            correctIndex = 0,
            explanation = "Live IoT dashboards deliver concurrent visibility so managers intervene on exceptions immediately rather than weeks later.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d10", part = M3Part.PART_D, topicId = "perf_predictive_analytics",
            topicTitle = "Predictive analytics", difficulty = QuestionDifficulty.MEDIUM,
            question = "Within the four-stage analytics maturity ladder, which stage estimates what is likely to happen next (such as customer churn probability)?",
            options = listOf("Descriptive analytics", "Diagnostic analytics", "Predictive analytics", "Historical archiving"),
            correctIndex = 2,
            explanation = "Descriptive = what happened; Diagnostic = why; Predictive = what will likely happen; Prescriptive = what action to take.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d11", part = M3Part.PART_D, topicId = "perf_budgetary_control",
            topicTitle = "Budgetary control", difficulty = QuestionDifficulty.MEDIUM,
            question = "Zero-Based Budgeting (ZBB) differs from incremental budgeting because ZBB:",
            options = listOf(
                "Simply adds an inflation percentage to last year's department budget",
                "Requires every activity and expense to be justified from scratch each cycle as a decision package",
                "Allows zero accountability for variances",
                "Applies only to manufacturing depreciation"
            ),
            correctIndex = 1,
            explanation = "ZBB evaluates every cost center from a zero base to eliminate legacy waste and align spending with current priorities.",
            sourceLabel = STUDY_TEXT_LABEL
        ),
        McqQuestion(
            id = "q_d12", part = M3Part.PART_D, topicId = "perf_csfs_vs_kpis",
            topicTitle = "Critical Success Factors (CSFs) vs KPIs", difficulty = QuestionDifficulty.MEDIUM,
            question = "Employee training hours completed this quarter (which drives future quality gains) is best classified as a:",
            options = listOf("Lagging financial indicator", "Leading indicator", "External failure cost", "Fixed overhead variance"),
            correctIndex = 1,
            explanation = "Leading indicators measure input activities/capabilities that predict future lagging outcomes like revenue or defect reduction.",
            sourceLabel = STUDY_TEXT_LABEL
        )
    )
}
