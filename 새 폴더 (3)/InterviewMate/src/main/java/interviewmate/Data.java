package interviewmate;

import java.awt.*;
import java.util.*;
import java.util.List;

/** 화면에 쓰이는 데이터 (실제 AI 대신 미리 준비한 예시 데이터) */
public final class Data {
    private Data() {}

    // ===== 면접관 성향 =====
    public static class Type {
        public final String name, short1, desc, sample;
        public final String[] tags;
        public final Color circle, body, hair, tagBg, tagFg;
        public final Faces.Expr expr;
        public final boolean glasses;
        public final String[] questions;
        Type(String name, String short1, String desc, String sample, String[] tags, Color circle, Color body, Color hair,
             Faces.Expr expr, boolean glasses, Color tagBg, Color tagFg, String[] questions) {
            this.name = name; this.short1 = short1; this.desc = desc; this.sample = sample; this.tags = tags;
            this.circle = circle; this.body = body; this.hair = hair; this.expr = expr; this.glasses = glasses;
            this.tagBg = tagBg; this.tagFg = tagFg; this.questions = questions;
        }
    }

    public static final Type[] TYPES = {
            new Type("따뜻한 멘토형", "멘", "편안하게 경험을 끌어내고 격려해 줘요",
                    "“천천히 말해도 괜찮아요. 그때 어떤 기분이었어요?”",
                    new String[]{"편안한 분위기", "경험 끌어내기", "격려"},
                    new Color(0xDCFCE7), new Color(0x3F6E4E), Faces.HAIR, Faces.Expr.SMILE, false,
                    new Color(0xDCFCE7), new Color(0x15803D),
                    new String[]{"편하게 자기소개 부탁드려요.", "가장 즐겁게 몰두했던 경험은 무엇인가요?",
                            "팀에서 갈등이 있었을 때 어떻게 풀었나요?", "최근에 새로 배운 것을 소개해 주세요.",
                            "실패했던 경험과 그 뒤에 달라진 점을 말해 주세요.", "우리 회사에 관심을 갖게 된 계기는요?",
                            "5년 뒤 어떤 개발자가 되고 싶나요?", "본인의 강점이 잘 드러난 순간은요?",
                            "스트레스를 받을 때 어떻게 관리하나요?", "마지막으로 하고 싶은 말이 있나요?"}),
            new Type("냉철한 압박형", "압", "답변의 허점을 짧고 날카롭게 파고들어요",
                    "“그건 팀의 성과죠. 본인이 한 일만 30초 안에 말해 보세요.”",
                    new String[]{"압박 질문", "짧은 반응", "꼬리질문 많음"},
                    new Color(0xFDE2E2), new Color(0x1F2937), Faces.HAIR, Faces.Expr.ANGRY, false,
                    new Color(0xFDE2E2), new Color(0xB91C1C),
                    new String[]{"1분 안에 자기소개 하세요.", "지원 동기, 다른 회사에도 똑같이 쓰셨죠?",
                            "그건 팀의 성과죠. 본인이 한 일만 30초 안에 말해 보세요.", "그 프로젝트, 실패한 거 아닌가요?",
                            "본인 단점 하나만, 변명 없이 말해 보세요.", "왜 우리가 당신을 뽑아야 하죠?",
                            "방금 답변, 근거가 뭔가요?", "더 좋은 지원자가 있다면 왜 당신이죠?",
                            "입사 후 1년 안에 그만두지 않을 근거는요?", "마지막 한마디, 10초 드립니다."}),
            new Type("논리 분석형", "논", "근거와 수치, 답변 구조를 꼼꼼히 봐요",
                    "“성능이 좋아졌다고 하셨는데, 몇 퍼센트였고 어떻게 측정했나요?”",
                    new String[]{"수치 확인", "구조화", "근거 요구"},
                    new Color(0xDBEAFE), new Color(0x334155), Faces.HAIR, Faces.Expr.FLAT, true,
                    new Color(0xDBEAFE), new Color(0x1D4ED8),
                    new String[]{"자기소개를 핵심 세 가지로 정리해 주세요.", "가장 큰 성과를 수치로 말해 주세요.",
                            "그 결과는 어떻게 측정했나요?", "문제를 정의한 과정부터 순서대로 설명해 주세요.",
                            "다른 대안은 무엇이 있었고 왜 버렸나요?", "트레이드오프를 판단한 기준은요?",
                            "실패 원인을 하나만 고른다면요?", "재발 방지를 위해 무엇을 바꿨나요?",
                            "우리 서비스의 문제점을 하나 분석해 보세요.", "답변을 30초로 요약해 주세요."}),
            new Type("유쾌한 대화형", "유", "대화하듯 가볍게 가치관과 컬처핏을 봐요",
                    "“요즘 제일 재밌게 본 것 하나만 소개해 주실래요?”",
                    new String[]{"가벼운 대화", "가치관", "컬처핏"},
                    new Color(0xFEF3C7), new Color(0x7C4A1E), Faces.HAIR, Faces.Expr.GRIN, false,
                    new Color(0xFEF3C7), new Color(0xB45309),
                    new String[]{"오늘 여기 오면서 무슨 생각 했어요?", "요즘 제일 재밌게 본 것 하나만요!",
                            "친구들은 당신을 어떤 사람이라고 해요?", "같이 일하기 싫은 동료 유형은요?",
                            "주말엔 주로 뭐 하세요?", "일할 때 꼭 지키는 나만의 규칙이 있나요?",
                            "팀 회식 메뉴 정하는 사람이 되면 뭐 고를래요?", "최근에 크게 웃었던 일은요?",
                            "우리 팀에 오면 제일 먼저 바꾸고 싶은 건요?", "마지막으로 궁금한 거 있어요?"}),
            new Type("깐깐한 임원형", "임", "회사 관점에서 판단력과 책임감을 따져요",
                    "“그 판단으로 회사가 손해를 봤다면, 책임은 누가 지나요?”",
                    new String[]{"판단력", "책임감", "회사 관점"},
                    new Color(0xE5E7EB), new Color(0x0F172A), Faces.GRAY_HAIR, Faces.Expr.FROWN, false,
                    new Color(0xE5E7EB), new Color(0x374151),
                    new String[]{"우리 회사가 왜 당신에게 돈을 써야 하죠?", "장애가 났을 때 가장 먼저 내릴 결정은요?",
                            "상사의 지시가 틀렸다면 어떻게 하겠어요?", "본인 실수로 매출이 줄었다면요?",
                            "10년 뒤 우리 회사는 어떻게 될까요?", "연봉 외에 회사에 바라는 건요?",
                            "동료가 무임승차하면 어떻게 하죠?", "가장 어려웠던 결정과 그 근거는요?",
                            "본인이 리더라면 무엇을 바꾸겠어요?", "합격하면 첫 3개월 계획은요?"})
    };
    public static final String[] JOBS = {"개발", "디자인", "마케팅", "영업", "공기업"};
    public static final int[] COUNTS = {3, 5, 10};

    // ===== 데일리 퀘스트 =====
    public static class Quest {
        public final String id, tag, title; public final int goal, reward; public int now; public boolean claimed;
        public final Color tagBg, tagFg;
        Quest(String id, String tag, String title, int now, int goal, int reward, boolean claimed, Color bg, Color fg) {
            this.id = id; this.tag = tag; this.title = title; this.now = now; this.goal = goal;
            this.reward = reward; this.claimed = claimed; this.tagBg = bg; this.tagFg = fg;
        }
        public boolean done() { return now >= goal; }
    }
    public static Quest[] defaultQuests() {
        return new Quest[]{
                new Quest("intro", "녹화", "1분 자기소개 녹화하기", 1, 1, 30, true, G.ORANGE_SOFT, G.ORANGE),
                new Quest("mock", "면접", "모의 면접 1회 완료하기", 1, 1, 50, false, G.BLUE_SOFT, G.BLUE),
                new Quest("press", "압박", "압박형 면접관과 3문항 답변", 1, 3, 50, false, G.RED_SOFT, G.RED),
                new Quest("gaze", "시선", "시선 처리 80점 이상 받기", 0, 1, 40, false, G.GREEN_SOFT, G.GREEN)
        };
    }

    // ===== 면접 기록 / 리포트 =====
    public static class QFeedback {
        public final String q, good, fix;
        QFeedback(String q, String good, String fix) { this.q = q; this.good = good; this.fix = fix; }
    }
    public static class Session {
        public final int type, count; public final boolean realtime; public String when;
        public final int[] scores; // 시선, 자세, 말 속도, 답변 구조, 습관어
        public final List<QFeedback> fb = new ArrayList<>();
        /** 면접 끝날 때 찍어 둔 카메라 화면 (없으면 null) */
        public java.awt.image.BufferedImage snap;
        Session(int type, int count, boolean realtime, String when, int[] scores) {
            this.type = type; this.count = count; this.realtime = realtime; this.when = when; this.scores = scores;
        }
        public int total() {
            int s = 0; for (int v : scores) s += v;
            return Math.round(s / (float) scores.length) + 3;   // 예시 데이터와 맞추기 위한 보정
        }
        public String title() { return TYPES[type].name + " · " + count + "문항"; }
        public String modeName() { return realtime ? "실시간 피드백" : "녹화 후 피드백"; }
    }
    public static final String[] SCORE_NAMES = {"시선 처리", "자세", "말 속도", "답변 구조", "습관어"};

    public static List<Session> defaultSessions() {
        List<Session> list = new ArrayList<>();
        Session a = new Session(1, 5, false, "어제", new int[]{68, 74, 82, 79, 61});
        String[] q = TYPES[1].questions;
        a.fb.add(new QFeedback(q[0], "첫 문장에서 이름과 직무를 바로 말해 집중을 끌었어요.", "60초를 넘겼어요. 경험은 하나만 골라 말해 보세요."));
        a.fb.add(new QFeedback(q[1], "회사의 서비스 이름을 정확히 언급했어요.", "다른 회사와 다른 이유를 한 문장으로 먼저 말하세요."));
        a.fb.add(new QFeedback(q[2], "“제가 맡은 부분은”으로 시작해 질문 의도에 맞게 답했어요.", "“음… 그러니까”가 5번 나왔어요. 결론부터 말하면 줄어요."));
        a.fb.add(new QFeedback(q[3], "실패를 인정하고 배운 점까지 말해 솔직해 보였어요.", "배운 점을 다음 프로젝트에 어떻게 썼는지 덧붙이세요."));
        a.fb.add(new QFeedback(q[4], "단점을 변명 없이 짧게 말했어요.", "고치려고 하는 구체적인 행동을 함께 말하세요."));
        list.add(a);
        Session b = new Session(0, 3, true, "2일 전", new int[]{80, 77, 75, 83, 72});
        String[] m = TYPES[0].questions;
        for (int i = 0; i < 3; i++) b.fb.add(new QFeedback(m[i], "편안한 표정으로 경험을 자연스럽게 풀어냈어요.", "마지막 문장을 배운 점으로 마무리해 보세요."));
        list.add(b);
        return list;
    }

    private static final String[] GOOD = {
            "결론을 먼저 말해 답변이 또렷했어요.", "질문한 의도를 정확히 짚었어요.", "수치를 들어 성과를 설명했어요.",
            "목소리에 힘이 있고 속도가 안정적이었어요.", "본인이 한 일과 팀이 한 일을 잘 나눴어요."};
    private static final String[] FIX = {
            "답변이 길어졌어요. 핵심 한 문장을 먼저 말하세요.", "시선이 아래로 4초 이상 머물렀어요.",
            "“음…” 같은 습관어가 자주 나왔어요.", "근거가 부족했어요. 숫자나 사례를 하나 넣어 보세요.",
            "마무리가 흐려졌어요. 배운 점으로 끝내 보세요."};

    /** 면접을 끝내면 예시 점수를 만들어 기록 (실제 서비스에선 AI 분석 결과가 들어갈 자리) */
    public static Session makeSession(int type, List<String> qs, boolean realtime, int answered) {
        Random r = new Random();
        int[] sc = new int[5];
        for (int i = 0; i < 5; i++) sc[i] = 60 + r.nextInt(30);
        if (realtime) sc[0] = Math.min(95, sc[0] + 6);  // 실시간 피드백은 시선 교정 효과
        Session s = new Session(type, qs.size(), realtime, "방금", sc);
        for (int i = 0; i < qs.size(); i++) {
            if (i < answered) s.fb.add(new QFeedback(qs.get(i), GOOD[r.nextInt(GOOD.length)], FIX[r.nextInt(FIX.length)]));
            else s.fb.add(new QFeedback(qs.get(i), "답변하지 않은 문항이에요.", "다음 연습에서 이 질문부터 시작해 보세요."));
        }
        return s;
    }

    // ===== 면접관 속마음 =====
    public static class Thought {
        public final String time, text, mood, reason; public final int moodLevel; // 0 강한호감 ~ 3 지루함
        Thought(String time, String text, String mood, String reason, int lvl) {
            this.time = time; this.text = text; this.mood = mood; this.reason = reason; this.moodLevel = lvl;
        }
    }
    public static final Thought[] THOUGHTS = {
            new Thought("00:07", "목소리에 힘이 있네. 일단 괜찮은 출발이야.", "호감", "첫 인사와 바른 자세", 1),
            new Thought("02:05", "질문을 받고 바로 눈을 피하네… 준비 안 된 건가?", "의심", "시선 회피 4초", 2),
            new Thought("04:31", "“음… 그러니까…” 결론이 언제 나오지?", "지루함", "습관어 5회, 결론 지연", 3),
            new Thought("07:48", "실패를 인정하고 배운 점까지 말하네. 솔직하다.", "호감", "책임 인정 + 배운 점", 1),
            new Thought("10:20", "우리 회사 과제를 공부해 왔구나. 같이 일해 보고 싶다.", "강한 호감", "회사와 경험의 연결", 0)
    };

    // ===== 돌발 상황 카드 =====
    public static final String[][] SURPRISES = {
            {"면접관 2명이 갑자기 들어왔어요", "이제부터 3:1 면접이에요. 새 면접관들이 서로 다른 관점에서 질문을 이어가요.",
                    "답할 때는 질문한 사람을 보고, 마무리는 전원을 한 번씩 보며 끝내세요."},
            {"면접관이 답변을 끊었어요", "“그래서 결론이 뭐죠?” 하고 말을 자릅니다. 10초 안에 핵심만 다시 말해야 해요.",
                    "당황하지 말고 “결론부터 말씀드리면”으로 바로 시작하세요."},
            {"갑자기 영어로 질문해요", "다음 질문은 영어로 이어집니다. 짧고 쉬운 문장으로 답해도 괜찮아요.",
                    "완벽한 문법보다 천천히, 또렷하게 말하는 것이 중요해요."},
            {"화면이 잠시 멈췄어요", "네트워크가 끊겨 면접관이 마지막 문장을 못 들었어요. 다시 요약해 주세요.",
                    "“다시 말씀드리면”으로 시작해 15초 안에 정리하세요."},
            {"예상 못 한 꼬리질문", "방금 말한 기술을 화이트보드에 그려 설명해 달라고 해요.",
                    "큰 그림 → 세부 순서로, 그리면서 말을 멈추지 마세요."}
    };

    // ===== 채용공고 분석 =====
    public static final String POSTING_DEFAULT = "[회사명] 백엔드 개발자 채용\n· 대용량 트래픽 API 설계 및 운영\n· 장애 대응과 모니터링 체계 개선\n· 우대: 결제 도메인 경험, 코드 리뷰 문화";
    public static final String RESUME_DEFAULT = "저는 어릴 때부터 컴퓨터에 관심이 많았습니다. 대학 팀 프로젝트에서 백엔드를 맡아 열심히 노력하여 좋은 결과를 얻었습니다. "
            + "이 경험으로 문제를 끝까지 파고드는 태도를 배웠습니다. 귀사의 대용량 트래픽 환경에서 최선을 다하겠습니다. "
            + "API 설계와 장애 대응, 모니터링 대시보드 구축, 코드 리뷰를 경험했고 Kafka로 쓰기를 비동기 처리했습니다.";

    /** 키워드, 찾을 단어들, 예상 질문, 근거 */
    public static final String[][] KEYWORDS = {
            {"대용량 트래픽", "트래픽|대용량", "트래픽이 갑자기 10배로 늘면 API에서 가장 먼저 무엇을 확인하시겠어요?", "대용량 트래픽 API 설계"},
            {"API 설계", "API", "좋은 API를 설계할 때 가장 중요하게 보는 기준은 무엇인가요?", "API 설계"},
            {"장애 대응", "장애", "직접 대응했던 장애 하나를 원인 파악부터 재발 방지까지 설명해 주세요.", "장애 대응과 모니터링"},
            {"모니터링", "모니터링", "모니터링 지표 중 꼭 알람을 걸어야 하는 것 세 가지는요?", "장애 대응과 모니터링"},
            {"결제 도메인", "결제", "결제 도메인 경험이 없는데, 어떻게 빠르게 따라잡으시겠어요?", "우대 사항 · 자소서 빈틈"},
            {"코드 리뷰", "코드 리뷰|리뷰", "코드 리뷰에서 의견이 부딪혔던 경험을 말해 주세요.", "코드 리뷰 문화"},
            {"데이터베이스", "DB|데이터베이스|SQL", "쿼리가 느려졌을 때 어떤 순서로 원인을 찾나요?", "데이터베이스"},
            {"클라우드", "AWS|클라우드|GCP", "클라우드 비용을 줄여 본 경험이 있나요?", "클라우드 운영"},
            {"협업", "협업|커뮤니케이션", "다른 직군과 협업하며 생긴 오해를 어떻게 풀었나요?", "협업"},
            {"자바", "Java|자바|Spring|스프링", "Spring에서 트랜잭션이 적용되지 않는 경우를 아시나요?", "기술 스택"}
    };

    // ===== 자기소개서 피드백 =====
    public static class Sentence {
        public String text; public int kind; // 0 보통, 1 고칠 문장, 2 살릴 문장
        public final String fixTitle, fixReason, suggestion;
        Sentence(String t, int k, String title, String reason, String sug) {
            text = t; kind = k; fixTitle = title; fixReason = reason; suggestion = sug;
        }
    }
    public static List<Sentence> resumeSection(int idx) {
        List<Sentence> s = new ArrayList<>();
        switch (idx) {
            case 0: {
                s.add(new Sentence("저는 어릴 때부터 컴퓨터에 관심이 많았습니다.", 1, "흔한 첫 문장",
                        "누구나 쓸 수 있는 문장이라 기억에 남지 않아요. 직무와 연결된 장면으로 시작하세요.",
                        "트래픽이 몰려 서버가 멈춘 날, 원인을 찾느라 밤을 새운 것이 백엔드를 택한 이유입니다."));
                s.add(new Sentence(" 대학 팀 프로젝트에서 백엔드를 맡아 ", 0, null, null, null));
                s.add(new Sentence("열심히 노력하여 좋은 결과를 얻었습니다.", 1, "추상적인 성과",
                        "“열심히”, “좋은 결과”는 확인할 수 없는 말이에요. 무엇이 얼마나 바뀌었는지 숫자로 보여주세요.",
                        "캐시 구조를 바꿔 평균 응답 시간을 3초에서 0.4초로 줄였습니다."));
                s.add(new Sentence(" 이 경험으로 문제를 끝까지 파고드는 태도를 배웠습니다.", 2, null, null, null));
                s.add(new Sentence(" 귀사의 대용량 트래픽 환경에서 ", 0, null, null, null));
                s.add(new Sentence("최선을 다하겠습니다.", 1, "막연한 포부",
                        "다짐보다 입사 후 할 일을 구체적으로 적으면 직무 연관성이 올라가요.",
                        "입사 후 1년 안에 주문 API의 응답 지연 알람 체계를 만들고 싶습니다."));
                break;
            }
            case 1: {
                s.add(new Sentence("저는 평범한 가정에서 자랐습니다.", 1, "성장 과정의 나열",
                        "가정 환경보다 지금의 나를 만든 사건 하나에 집중하세요.",
                        "고등학교 때 학교 축제 예매 사이트를 직접 만들며 처음으로 서버를 다뤘습니다."));
                s.add(new Sentence(" 부모님께서는 항상 책임감을 강조하셨습니다.", 0, null, null, null));
                s.add(new Sentence(" 동아리 회장을 맡아 30명의 일정을 조율하며 갈등을 줄이는 방법을 배웠습니다.", 2, null, null, null));
                break;
            }
            default: {
                s.add(new Sentence("Spring Boot로 REST API를 만들었습니다.", 0, null, null, null));
                s.add(new Sentence(" Kafka로 쓰기를 비동기 처리했습니다.", 2, null, null, null));
                s.add(new Sentence(" 다양한 기술을 사용할 수 있습니다.", 1, "근거 없는 역량",
                        "기술 이름만 나열하면 깊이를 알 수 없어요. 하나라도 문제 해결 경험과 묶어 주세요.",
                        "Redis 캐시를 도입해 조회 API의 DB 부하를 60% 줄였습니다."));
            }
        }
        return s;
    }
    public static final String[] RESUME_TABS = {"1. 지원동기", "2. 성장 과정", "3. 직무 역량"};
    public static final String[] RESUME_TITLES = {"지원 동기와 입사 후 포부", "성장 과정", "직무 역량"};
    public static final int[][] RESUME_SCORES = {{62, 70, 81, 58}, {55, 48, 84, 52}, {71, 85, 76, 63}};

    // ===== 자소서 예상 질문 =====
    public static class Expected {
        public final String cat, from, quote, q, guide; public final boolean priority;
        Expected(String cat, String from, String quote, String q, String guide, boolean p) {
            this.cat = cat; this.from = from; this.quote = quote; this.q = q; this.guide = guide; this.priority = p;
        }
    }
    public static final Expected[] EXPECTED = {
            new Expected("경험 검증", "1. 지원동기", "“평균 응답 시간을 3초에서 0.4초로 줄였습니다.”",
                    "0.4초는 어떤 조건에서 측정한 값인가요? 병목을 어떻게 찾으셨어요?",
                    "측정 환경(트래픽, 측정 도구)을 먼저 밝히고, 병목을 찾은 순서를 2~3단계로 말하세요. 혼자 한 일과 팀이 한 일을 구분하세요.", true),
            new Expected("꼬리질문", "3. 직무 역량", "“Kafka로 쓰기를 비동기 처리했습니다.”",
                    "메시지가 중복되거나 유실되면 어떻게 처리하셨나요?",
                    "멱등성 키, 재시도 정책, DLQ 중 실제로 쓴 방법을 하나 골라 이유와 함께 설명하세요.", false),
            new Expected("약점 공략", "공고 비교", "우대 사항: 결제 도메인 경험",
                    "결제 도메인 경험이 없는데, 어떻게 빠르게 따라잡으시겠어요?",
                    "없다는 사실은 짧게 인정하고, 비슷한 경험(정합성이 중요했던 기능)을 연결한 뒤 학습 계획을 말하세요.", true),
            new Expected("경험 검증", "2. 성장 과정", "“동아리 회장을 맡아 30명의 일정을 조율하며…”",
                    "일정 조율이 실패했던 순간은 없었나요? 그때 어떻게 했나요?",
                    "실패 장면 하나를 고르고, 내가 바꾼 규칙 하나와 그 결과를 말하세요.", false),
            new Expected("꼬리질문", "1. 지원동기", "“문제를 끝까지 파고드는 태도를 배웠습니다.”",
                    "끝까지 파고들다 일정이 늦어진 적은 없나요? 그럴 땐 어떻게 판단하죠?",
                    "깊이와 일정 사이에서 멈출 기준(시간 상한, 공유 시점)을 구체적으로 말하세요.", false)
    };

    // ===== 기업 면접 정보 =====
    public static final String[][] COMPANY_STEPS = {
            {"서류 전형", "자소서 3문항. 직무 경험 문항 비중이 커요."},
            {"코딩 테스트", "온라인 90분 · 알고리즘 2문제와 SQL 1문제"},
            {"1차 실무 면접", "실무자 3명 · 60분 · 프로젝트 깊이 파기"},
            {"2차 임원 면접", "임원 2명 · 40분 · 장애 상황 판단과 가치관"}
    };
    public static final String[] COMPANY_QUESTIONS = {
            "가장 큰 장애와 그때 내린 판단을 설명해 주세요.", "트랜잭션 격리 수준을 바꿔 본 경험이 있나요?",
            "캐시와 DB의 정합성은 어떻게 맞췄나요?", "코드 리뷰에서 받은 가장 아픈 피드백은요?",
            "왜 이직(지원)하려고 하나요?"
    };
    public static final String REVIEW_PREVIEW = "2차 면접은 임원 2명과 40분 동안 진행됐고, 처음 10분은 이력서 대신 ";
    public static final String REVIEW_FULL = "2차 면접은 임원 2명과 40분 동안 진행됐고, 처음 10분은 이력서 대신 "
            + "실제 장애 시나리오를 주고 판단 순서를 물었습니다. 정답보다 '누구에게 먼저 알리는지', '언제 롤백을 결정하는지' 같은 "
            + "판단 기준을 계속 확인했어요. 후반에는 협업 가치관과 코드 리뷰 문화에 대한 질문이 이어졌고, 분위기는 생각보다 편안했습니다.";

    public static List<String> questionsFor(int type, int count) {
        List<String> q = new ArrayList<>();
        String[] all = TYPES[type].questions;
        for (int i = 0; i < count; i++) q.add(all[i % all.length]);
        return q;
    }
}
