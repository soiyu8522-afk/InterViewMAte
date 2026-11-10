package interviewmate;

import java.awt.*;

/** 2. 면접관 선택 (1/2 단계) */
public class SelectScreen extends Page {
    public SelectScreen() { super("select", G.BG, 100, App.H - 86, 560); }

    @Override public void onShow() { scrollTop(); }

    @Override protected void drawFixed(Graphics2D g) {
        App.header(g, this, "어떤 면접관과 연습할까요?",
                App.customQuestions != null ? "1 / 2 단계 · 맞춤 질문 " + App.customQuestions.size() + "개로 면접을 봐요"
                        : "1 / 2 단계 · 성향에 따라 질문 방식과 반응이 달라져요", G.INK, G.SLATE);
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        Data.Type t = Data.TYPES[App.interviewer];
        G.text(g, "선택한 면접관", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, t.name + " · " + Data.JOBS[App.job] + " · "
                + (App.customQuestions != null ? App.customQuestions.size() : App.questionCount) + "문항", 40, App.H - 46, G.b(15), G.INK);
        App.arrowButton(g, this, 760, App.H - 70, 260, 50, "다음: 피드백 방식 고르기", G.BLUE, () -> App.go("feedback"));
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // 성향 카드 4개
        for (int i = 0; i < 4; i++) card(g, b, i, 40 + i * 249, 12, 233, 186);

        // 잠금 해제형 면접관
        double y = 214;
        Data.Type ex = Data.TYPES[4];
        boolean sel = App.interviewer == 4;
        if (sel) G.rr(g, 37, y - 3, 986, 76, 18, new Color(0xDCE4FB));
        G.rr(g, 40, y, 980, 70, 16, G.WHITE);
        if (sel) G.rrLine(g, 40, y, 980, 70, 16, G.BLUE, 2f);
        Faces.avatar(g, 78, y + 35, 22, ex);
        if (App.executiveUnlocked) {
            G.text(g, ex.name, 114, y + 15, G.b(15), G.INK);
            G.text(g, ex.desc, 114, y + 39, G.f(12.5f), G.SUB);
            G.radio(g, 992, y + 35, 10, sel);
            b.on(40, y, 980, 70, () -> App.interviewer = 4);
        } else {
            G.text(g, ex.name + "  · 잠김", 114, y + 15, G.b(15), G.SLATE);
            G.text(g, "주말 보스전을 클리어하면 열려요", 114, y + 39, G.f(12.5f), G.SUB);
            G.tag(g, "보스전 보러 가기", 880, y + 22, 26, G.b(12), G.ORANGE_SOFT, G.ORANGE, 11, 13);
            b.on(40, y, 980, 70, () -> App.go("boss"));
        }
        y += 92;

        // 질문 예시 (왼쪽)
        Data.Type t = Data.TYPES[App.interviewer];
        G.rr(g, 40, y, 470, 160, 16, G.WHITE);
        G.text(g, "이 면접관의 질문 예시", 62, y + 22, G.b(12.5f), G.SLATE);
        int qh = G.wrap(g, t.sample, 62, y + 52, 426, G.b(16), G.INK, 25);
        double tx = 62;
        for (String tag : t.tags) tx += G.pill(g, tag, tx, y + 62 + qh, 26, G.b(12), t.tagBg, t.tagFg, 11) + 8;

        // 지원 직무 / 질문 개수 (오른쪽)
        double rx = 550;
        G.text(g, "지원 직무", rx, y, G.b(15), G.INK);
        double cx = rx;
        for (int i = 0; i < Data.JOBS.length; i++) {
            int w = G.width(g, Data.JOBS[i], G.b(14)) + 34;
            chip(g, Data.JOBS[i], cx, y + 28, w, App.job == i);
            final int k = i;
            b.on(cx, y + 28, w, 37, () -> App.job = k);
            cx += w + 8;
        }
        G.text(g, "질문 개수", rx, y + 90, G.b(15), G.INK);
        if (App.customQuestions != null) {
            G.text(g, "맞춤 질문 " + App.customQuestions.size() + "개가 그대로 쓰여요", rx, y + 126, G.f(14), G.SUB);
        } else {
            cx = rx;
            for (int i = 0; i < Data.COUNTS.length; i++) {
                String s = Data.COUNTS[i] + "문항";
                int w = G.width(g, s, G.b(14)) + 34;
                chip(g, s, cx, y + 118, w, App.questionCount == Data.COUNTS[i]);
                final int n = Data.COUNTS[i];
                b.on(cx, y + 118, w, 37, () -> App.questionCount = n);
                cx += w + 8;
            }
        }
        b.height((int) y + 180);
    }

    private void card(Graphics2D g, Body b, int i, double x, double y, double w, double h) {
        Data.Type t = Data.TYPES[i];
        boolean sel = App.interviewer == i;
        if (sel) G.rr(g, x - 3, y - 3, w + 6, h + 6, 19, new Color(0xDCE4FB));
        G.rr(g, x, y, w, h, 16, G.WHITE);
        if (sel) G.rrLine(g, x, y, w, h, 16, G.BLUE, 2f);
        Faces.avatar(g, x + 52, y + 52, 32, t);
        G.radio(g, x + w - 26, y + 26, 10.5, sel);
        G.text(g, t.name, x + 20, y + 100, G.b(17), G.INK);
        G.wrap(g, t.desc, x + 20, y + 130, w - 40, G.f(13), G.SUB, 19);
        b.on(x, y, w, h, () -> App.interviewer = i);
    }

    static void chipSmall(Graphics2D g, String s, double x, double y, double w, boolean on) {
        if (on) {
            G.rr(g, x, y, w, 37, 18.5, G.INK);
            G.textBox(g, s, x, y, w, 37, G.b(13), G.WHITE);
        } else {
            G.rr(g, x, y, w, 37, 18.5, G.WHITE);
            G.rrLine(g, x, y, w, 37, 18.5, G.LINE, 1.2f);
            G.textBox(g, s, x, y, w, 37, G.b(13), G.TEXT);
        }
    }

    static void chip(Graphics2D g, String s, double x, double y, double w, boolean on) {
        if (on) {
            G.rr(g, x, y, w, 37, 18.5, G.INK);
            G.textBox(g, s, x, y, w, 37, G.b(14), G.WHITE);
        } else {
            G.rr(g, x, y, w, 37, 18.5, G.WHITE);
            G.rrLine(g, x, y, w, 37, 18.5, G.LINE, 1.2f);
            G.textBox(g, s, x, y, w, 37, G.b(14), G.TEXT);
        }
    }
}
