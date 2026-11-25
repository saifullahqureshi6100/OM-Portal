package com.example.data

import com.example.model.M3Part
import com.example.model.SyllabusTopic
import com.example.model.TopicDifficulty

internal object SyllabusPartCD {
    val partCTopics: List<SyllabusTopic> = listOf(
        SyllabusTopic(
            id = "mkt_marketing_philosophies",
            part = M3Part.PART_C,
            title = "Marketing philosophies",
            subtitle = "Production, Product, Selling, Marketing & Holistic concepts",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Marketing philosophies (orientations) represent the five competing mindsets guiding how an organization balances company goals, customer needs, and societal welfare.",
            keyConcepts = listOf(
                "1. Production Concept: focus on high efficiency, mass scale, and low price (risks ignoring quality/needs).",
                "2. Product Concept: focus on technical perfection/features ('better mousetrap' -> risks Marketing Myopia).",
                "3. Selling Concept: aggressive inside-out persuasion to push existing inventory (short-term, unsought goods).",
                "4. Marketing Concept: outside-in understanding of target customer needs better than rivals.",
                "5. Societal / Holistic Marketing: customer satisfaction + long-run societal & environmental well-being."
            ),
            examples = listOf(
                "Selling concept: pushy door-to-door sales; Marketing concept: designing an app around verified user pain points."
            ),
            revisionNotes = listOf(
                "Marketing Myopia (Theodore Levitt) = obsessing over the product instead of the underlying customer benefit!",
                "Selling = company needs to sell what it makes; Marketing = company makes what customers need."
            ),
            keyFramework = "Kotler's 5 Marketing Orientations"
        ),
        SyllabusTopic(
            id = "mkt_marketing_environment",
            part = M3Part.PART_C,
            title = "Marketing environment",
            subtitle = "Micro-environment actors & Macro-environment forces",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "The marketing environment comprises internal and external actors and forces—divided into the task Micro-environment and broad Macro-environment—affecting a firm's ability to build profitable customer relationships.",
            keyConcepts = listOf(
                "Micro-environment (close actors): Company departments, Suppliers, Marketing Intermediaries, Competitors, Publics, Customers.",
                "Macro-environment (uncontrollable broad forces): Demographic, Economic, Natural, Technological, Political, Cultural (PESTEL).",
                "Proactive vs. reactive environmental response."
            ),
            examples = listOf(
                "A supermarket renegotiating cold-chain distributors (Micro) while adapting product packs to inflation (Macro Economic)."
            ),
            revisionNotes = listOf(
                "Micro = 6 immediate task actors; Macro = broader societal/PESTEL forces."
            ),
            keyFramework = "Micro (6 Actors) vs. Macro Environment"
        ),
        SyllabusTopic(
            id = "mkt_corporate_vs_marketing",
            part = M3Part.PART_C,
            title = "Corporate vs. marketing strategy",
            subtitle = "BCG Matrix, Ansoff Growth Matrix & STP positioning",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Corporate strategy defines the overall portfolio and long-term direction of the enterprise (e.g., BCG, Ansoff), whereas marketing strategy operationalizes how specific business units win target segments via STP and the 4Ps/7Ps.",
            keyConcepts = listOf(
                "Corporate level: mission, SBU portfolio (BCG: Stars, Cash Cows, Question Marks, Dogs), Ansoff Matrix (Market Penetration, Market Development, Product Development, Diversification).",
                "Marketing level: Segmentation -> Targeting -> Positioning (STP) + Marketing Mix (Product, Price, Place, Promotion + People, Process, Physical Evidence)."
            ),
            examples = listOf(
                "Corporate strategy allocates Cash Cow profits to fund a high-growth Question Mark SBU; Marketing strategy positions the Star brand via STP."
            ),
            revisionNotes = listOf(
                "Ansoff lowest risk = Market Penetration (existing product, existing market); highest risk = Diversification (new/new).",
                "Services expand 4Ps to 7Ps (+ People, Process, Physical Evidence)."
            ),
            keyFramework = "Ansoff · BCG · STP -> 4Ps/7Ps"
        ),
        SyllabusTopic(
            id = "mkt_metaverse_virtual",
            part = M3Part.PART_C,
            title = "Metaverse and virtual branding",
            subtitle = "Immersive 3D spaces, digital twins & virtual storefronts",
            difficulty = TopicDifficulty.CORE,
            definition = "Metaverse and virtual branding engage consumers inside persistent 3D virtual worlds, AR/VR experiences, and gamified digital environments through interactive brand activations and virtual goods.",
            keyConcepts = listOf(
                "Direct-to-Avatar (D2A) commerce: virtual wearables, skins, digital showrooms.",
                "Experiential engagement with Gen Z/Alpha audiences in platforms like Roblox, Fortnite, or VR showrooms.",
                "Risks: fragmented platforms, hardware adoption barriers, brand-safety moderation, unclear short-term ROI."
            ),
            examples = listOf(
                "Nike's Nikeland or luxury fashion houses hosting virtual runway drops and try-on AR filters."
            ),
            revisionNotes = listOf(
                "Metaverse marketing prioritizes experiential co-creation over passive banner ads."
            ),
            keyFramework = "D2A Commerce & Immersive Experiential Branding"
        ),
        SyllabusTopic(
            id = "mkt_micro_influencers",
            part = M3Part.PART_C,
            title = "Micro-influencer partnerships",
            subtitle = "Niche trust, engagement ROI & FTC/ASA disclosure",
            difficulty = TopicDifficulty.CORE,
            definition = "Micro-influencer partnerships collaborate with niche content creators (typically 10,000–100,000 followers) who command high audience trust and engagement rates compared to broad celebrity endorsers.",
            keyConcepts = listOf(
                "Higher engagement-to-cost ratio, authentic peer credibility, hyper-targeted demographics.",
                "Selection metrics: genuine engagement rate, audience fit, brand safety, fake-follower screening.",
                "Legal/ethical obligation: transparent paid-partnership (#ad / #sponsored) disclosure."
            ),
            examples = listOf(
                "A skincare brand partnering with 25 local dermatology/lifestyle micro-creators instead of one expensive film star."
            ),
            revisionNotes = listOf(
                "Micro-influencers win on authenticity & engagement; mega-influencers win on raw mass reach."
            ),
            keyFramework = "Reach vs. Niche Engagement Efficiency"
        ),
        SyllabusTopic(
            id = "mkt_brand_management",
            part = M3Part.PART_C,
            title = "Brand management",
            subtitle = "Brand equity (Aaker/Keller), architecture & extensions",
            difficulty = TopicDifficulty.CORE,
            definition = "Brand management builds, measures, and protects brand equity—the differential value a recognized brand name adds to a product beyond its functional utility.",
            keyConcepts = listOf(
                "Aaker Brand Equity Dimensions: Awareness, Associations, Perceived Quality, Loyalty, Proprietary Assets.",
                "Brand Architecture: House of Brands (P&G: Tide, Ariel, Pampers) vs. Branded House (Google, Virgin).",
                "Line Extension vs. Brand Extension (risk of brand dilution if stretched too far)."
            ),
            examples = listOf(
                "Unilever using House of Brands to isolate reputation risk and serve distinct price tiers."
            ),
            revisionNotes = listOf(
                "Brand equity lets firms charge price premiums and weather competitive shocks.",
                "Watch brand dilution when stretching luxury or specialist brands into unrelated categories."
            ),
            keyFramework = "Aaker Equity Model & Brand Architecture"
        ),
        SyllabusTopic(
            id = "mkt_imc",
            part = M3Part.PART_C,
            title = "Integrated marketing communications (IMC)",
            subtitle = "Unified omnichannel messaging & promotional mix",
            difficulty = TopicDifficulty.CORE,
            definition = "IMC coordinates all promotional channels—advertising, sales promotion, PR, personal selling, direct/digital marketing, and social media—to deliver a clear, consistent, and compelling brand message.",
            keyConcepts = listOf(
                "5 Promotional Mix Tools: Advertising, Sales Promotion, Public Relations, Personal Selling, Direct & Digital Marketing.",
                "Push Strategy (promote through channel intermediaries) vs. Pull Strategy (stimulate end-consumer demand directly).",
                "Eliminates contradictory claims across offline, mobile, and in-store touchpoints."
            ),
            examples = listOf(
                "A telecom launch synchronizing TV spots, influencer reels, SMS promos, retail signage, and sales-rep scripts around one tagline."
            ),
            revisionNotes = listOf(
                "IMC = 'One voice, one look' across every customer touchpoint.",
                "Know Push (B2B channel incentives) vs. Pull (B2C mass ads pulling stock through channels)."
            ),
            keyFramework = "5 Promotion Tools + Push/Pull"
        ),
        SyllabusTopic(
            id = "mkt_consumer_behavior",
            part = M3Part.PART_C,
            title = "Consumer behavior",
            subtitle = "5-stage buying process, psychological drivers & dissonance",
            difficulty = TopicDifficulty.CORE,
            definition = "Consumer behavior studies how individuals and households select, purchase, use, and dispose of goods and services, shaped by cultural, social, personal, and psychological factors.",
            keyConcepts = listOf(
                "5-Stage Decision Process: Need Recognition -> Information Search -> Evaluation of Alternatives -> Purchase Decision -> Post-Purchase Behavior.",
                "Post-purchase cognitive dissonance ('buyer's remorse') mitigated via warranties, follow-ups, and reassuring communication.",
                "High-involvement (complex/expensive) vs. Habitual/Variety-seeking buying."
            ),
            examples = listOf(
                "Automated post-purchase onboarding emails and 30-day return guarantees reducing buyer's remorse for laptops."
            ),
            revisionNotes = listOf(
                "Buying roles: Initiator, Influencer, Decider, Buyer, User.",
                "Cognitive dissonance peaks right after high-involvement purchases."
            ),
            keyFramework = "5-Stage Buyer Journey & Involvement Grid"
        ),
        SyllabusTopic(
            id = "mkt_npo_marketing",
            part = M3Part.PART_C,
            title = "NPO marketing",
            subtitle = "Non-profit dual-constituency fundraising & mission value",
            difficulty = TopicDifficulty.CORE,
            definition = "Non-Profit Organization (NPO) marketing applies marketing strategy to attract donors, volunteers, and public support while delivering social services to beneficiaries without profit distribution.",
            keyConcepts = listOf(
                "Dual Audience Challenge: Resource attraction (donors, grants, volunteers) + Resource allocation (beneficiaries/clients).",
                "Success measured by social impact, mission reach, and donor retention rather than shareholder dividends.",
                "Transparency and emotional storytelling drive recurring donations."
            ),
            examples = listOf(
                "A charity hospital publishing audited impact reports showing cost-per-surgery to retain corporate & individual donors."
            ),
            revisionNotes = listOf(
                "Key distinction: NPOs serve two markets simultaneously—funders and beneficiaries."
            ),
            keyFramework = "Dual-Constituency NPO Model"
        ),
        SyllabusTopic(
            id = "mkt_internal_marketing",
            part = M3Part.PART_C,
            title = "Internal marketing",
            subtitle = "Employees as internal customers & service-profit chain",
            difficulty = TopicDifficulty.CORE,
            definition = "Internal marketing treats employees as internal customers—recruiting, training, motivating, and aligning staff with the brand promise before or alongside external marketing campaigns.",
            keyConcepts = listOf(
                "Frontline staff embody the service brand ('People' in the 7Ps).",
                "Service-Profit Chain: Internal service quality -> Employee satisfaction/retention -> External service value -> Customer loyalty -> Profit.",
                "Prevents the gap between glossy external ads and poor frontline attitude."
            ),
            examples = listOf(
                "Briefing hotel staff on a new hospitality promise and empowering them to resolve guest issues before launching TV ads."
            ),
            revisionNotes = listOf(
                "External promises fail without internal alignment!",
                "Internal marketing bridges HR and Marketing."
            ),
            keyFramework = "Service–Profit Chain"
        ),
        SyllabusTopic(
            id = "mkt_social_marketing",
            part = M3Part.PART_C,
            title = "Social marketing",
            subtitle = "Behavior change for public health, safety & sustainability",
            difficulty = TopicDifficulty.CORE,
            definition = "Social marketing uses commercial marketing principles and the marketing mix to influence voluntary behavior change that benefits individuals and society (distinct from commercial 'social media marketing').",
            keyConcepts = listOf(
                "Goal: sustained behavioral change (e.g., vaccination, seatbelts, anti-smoking, water conservation)—not commercial profit.",
                "Adapts 4Ps: Desired behavior (Product), Effort/psychological cost (Price), Access points (Place), Behavior-nudge messaging (Promotion)."
            ),
            examples = listOf(
                "A national campaign encouraging seatbelt compliance by combining emotional PSAs with accessible child-seat subsidies."
            ),
            revisionNotes = listOf(
                "EXAM TRAP: Social Marketing (public-good behavior change) != Social Media Marketing (advertising on Instagram/TikTok)!"
            ),
            keyFramework = "Behavior-Change 4Ps"
        ),
        SyllabusTopic(
            id = "mkt_web3_nft",
            part = M3Part.PART_C,
            title = "Web3 / NFT-based campaigns",
            subtitle = "Token-gated loyalty, digital collectibles & decentralized trust",
            difficulty = TopicDifficulty.CORE,
            definition = "Web3 and NFT-based campaigns leverage blockchain-verified digital collectibles, smart contracts, and token-gated communities to reward loyal customers with verifiable ownership and exclusive perks.",
            keyConcepts = listOf(
                "Use cases: verifiable loyalty passes, event access tokens, anti-counterfeit luxury provenance, co-created brand communities.",
                "Critiques & Risks: crypto-wallet UX friction, price volatility/speculation backlash, regulatory scrutiny, environmental perceptions."
            ),
            examples = listOf(
                "Starbucks Odyssey / sneaker brands issuing digital provenance tokens unlocking VIP drops and real-world events."
            ),
            revisionNotes = listOf(
                "Sustainable Web3 campaigns focus on real customer utility & loyalty access—not speculative hype."
            ),
            keyFramework = "Utility-Driven Token Loyalty"
        )
    )

    val partDTopics: List<SyllabusTopic> = listOf(
        SyllabusTopic(
            id = "perf_control_systems",
            part = M3Part.PART_D,
            title = "Control systems",
            subtitle = "Feedforward, concurrent & feedback closed-loop control",
            difficulty = TopicDifficulty.FOUNDATION,
            definition = "Management control systems monitor organizational activities against planned standards, identify variances, and trigger corrective actions to ensure strategic and operational goals are met.",
            keyConcepts = listOf(
                "4-Step Control Loop: Set standards -> Measure actual performance -> Compare & analyze variances -> Take corrective action.",
                "Timing Types: Feedforward (preventive before input), Concurrent (real-time during process), Feedback (post-output review).",
                "Anthony's Hierarchy: Strategic Planning, Management Control, Operational Control."
            ),
            examples = listOf(
                "Feedforward: vendor quality audit before materials ship. Concurrent: live call-center SLA monitor. Feedback: monthly variance report."
            ),
            revisionNotes = listOf(
                "Feedforward prevents errors before they happen; Feedback learns after output; Concurrent corrects in real time."
            ),
            keyFramework = "Feedforward · Concurrent · Feedback Loop"
        ),
        SyllabusTopic(
            id = "perf_csfs_vs_kpis",
            part = M3Part.PART_D,
            title = "Critical Success Factors (CSFs) vs KPIs",
            subtitle = "Qualitative strategic pillars vs quantifiable metrics",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Critical Success Factors (CSFs) are the essential areas where an organization MUST perform well to achieve its mission, while Key Performance Indicators (KPIs) are the quantifiable metrics measuring progress on those CSFs.",
            keyConcepts = listOf(
                "CSFs = 'What must go right?' (strategic, qualitative capability/condition, e.g., Fast Order Fulfillment).",
                "KPIs = 'How do we measure it?' (quantifiable, time-bound, SMART metric, e.g., % orders shipped within 24 hours >= 98%).",
                "Leading indicators (predict future performance) vs. Lagging indicators (measure past outcomes)."
            ),
            examples = listOf(
                "CSF: High Customer Retention -> KPI: Quarterly Churn Rate < 3% & Net Promoter Score (NPS) >= 55."
            ),
            revisionNotes = listOf(
                "Never confuse CSF (the vital outcome/area) with KPI (the numeric gauge)! Every CSF needs 1–3 aligned KPIs."
            ),
            keyFramework = "CSF (What) -> KPI (Measure)"
        ),
        SyllabusTopic(
            id = "perf_realtime_iot_dashboards",
            part = M3Part.PART_D,
            title = "Real-time IoT dashboards",
            subtitle = "Live telemetry, anomaly alerts & exception management",
            difficulty = TopicDifficulty.CORE,
            definition = "Real-time IoT dashboards aggregate streaming sensor, machine, and logistics telemetry into visual KPI cockpits enabling instant concurrent control and rapid exception handling.",
            keyConcepts = listOf(
                "Replaces delayed month-end batch reports with continuous operational visibility.",
                "Tracks OEE, cold-chain temperatures, energy draw, fleet GPS, and SLA queues live.",
                "Pitfall: information overload—dashboards must highlight exceptions and actionable thresholds."
            ),
            examples = listOf(
                "Cold-chain vaccine trucks streaming live temperature alerts to operations controllers if refrigeration drifts above 8°C."
            ),
            revisionNotes = listOf(
                "IoT dashboards power Concurrent Control and Management by Exception."
            ),
            keyFramework = "IoT Telemetry -> Exception Cockpit"
        ),
        SyllabusTopic(
            id = "perf_budgetary_control",
            part = M3Part.PART_D,
            title = "Budgetary control",
            subtitle = "Flexible budgets, variance analysis, ZBB & behavioral risks",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Budgetary control compares actual revenues and costs against budgeted targets to pinpoint responsibility-center variances and guide resource discipline.",
            keyConcepts = listOf(
                "Fixed vs. Flexible Budgets (flexed to actual activity volume for fair efficiency comparison).",
                "Incremental vs. Zero-Based Budgeting (ZBB: every expense justified from zero each cycle).",
                "Behavioral issues: budgetary slack ('padding'), short-termism, departmental silo gaming, 'spend-it-or-lose-it' mentality."
            ),
            examples = listOf(
                "Flexing a factory overhead budget to actual 8,500 units before judging a production manager's cost variance."
            ),
            revisionNotes = listOf(
                "Always compare actual costs against a FLEXED budget for true efficiency control.",
                "Past-paper favorite: pros/cons of budgetary control and dysfunctional gaming."
            ),
            keyFramework = "Flexible Budget + ZBB + Variance Feedback"
        ),
        SyllabusTopic(
            id = "perf_financial_nonfinancial_tools",
            part = M3Part.PART_D,
            title = "Financial and non-financial tools",
            subtitle = "ROI, RI, EVA, Fitzgerald-Moon & Performance Pyramid",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Modern performance management blends traditional financial ratios (ROI, Residual Income, EVA) with non-financial operational indicators (quality, cycle time, churn, innovation) to prevent short-term manipulation.",
            keyConcepts = listOf(
                "Financial: ROI (risk of rejecting good projects above cost of capital but below current division ROI), Residual Income (RI = Profit - Imputed Interest), EVA.",
                "Non-Financial: On-time delivery, defect rates, NPS, employee turnover, R&D pipeline.",
                "Fitzgerald & Moon (services): Results (Financial + Competitiveness) vs. Determinants (Quality, Flexibility, Resource Utilization, Innovation)."
            ),
            examples = listOf(
                "Using Residual Income instead of ROI so a high-performing division still accepts a 14% project when cost of capital is 10%."
            ),
            revisionNotes = listOf(
                "Financial-only metrics encourage cutting R&D/training to inflate this year's profit!",
                "RI solves ROI's goal-congruence rejection trap."
            ),
            keyFramework = "ROI/RI/EVA + Fitzgerald & Moon"
        ),
        SyllabusTopic(
            id = "perf_balanced_scorecard",
            part = M3Part.PART_D,
            title = "Balanced Scorecard",
            subtitle = "Kaplan & Norton's 4 perspectives & cause-effect strategy map",
            difficulty = TopicDifficulty.ADVANCED,
            definition = "Developed by Kaplan and Norton, the Balanced Scorecard translates corporate strategy into a balanced set of objectives and measures across four linked perspectives: Financial, Customer, Internal Business Processes, and Learning & Growth.",
            keyConcepts = listOf(
                "1. Financial: How do we look to shareholders? (ROCE, revenue growth, cash flow).",
                "2. Customer: How do customers see us? (NPS, market share, retention).",
                "3. Internal Business Processes: What must we excel at? (cycle time, defect rate, unit cost).",
                "4. Learning & Growth: How can we continue to improve and create value? (training, IT, culture).",
                "Limitations (past paper!): conflict/trade-offs between perspectives, measurement overload, lack of single bottom-line weighting."
            ),
            examples = listOf(
                "Training staff (Learning & Growth) speeds up order processing (Internal Process), lifting retention (Customer) and margins (Financial)."
            ),
            revisionNotes = listOf(
                "Memorize all 4 perspectives & bottom-up cause-effect chain: Learning -> Internal -> Customer -> Financial!",
                "Past-paper favorite: advantages AND drawbacks/disadvantages of Balanced Scorecard."
            ),
            keyFramework = "Kaplan & Norton 4 Perspectives"
        ),
        SyllabusTopic(
            id = "perf_predictive_analytics",
            part = M3Part.PART_D,
            title = "Predictive analytics",
            subtitle = "Forecasting churn, demand & financial risk via ML",
            difficulty = TopicDifficulty.CORE,
            definition = "Predictive analytics applies statistical modeling and machine learning to historical and real-time operational data to forecast future demand, customer churn, credit defaults, and equipment failures.",
            keyConcepts = listOf(
                "Analytics Maturity: Descriptive (what happened) -> Diagnostic (why) -> Predictive (what is likely next) -> Prescriptive (what action to take).",
                "Methods: regression, time-series forecasting, classification trees, propensity scoring.",
                "Governance risks: overfitting, data bias, false confidence without domain judgment."
            ),
            examples = listOf(
                "Telco predicting prepaid subscriber churn 14 days ahead and triggering targeted retention bundles."
            ),
            revisionNotes = listOf(
                "Know the 4-stage ladder: Descriptive -> Diagnostic -> Predictive -> Prescriptive."
            ),
            keyFramework = "4-Stage Analytics Ladder"
        )
    )
}
