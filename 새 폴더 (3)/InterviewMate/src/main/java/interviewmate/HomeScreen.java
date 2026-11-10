package interviewmate;

import java.awt.*;
import java.time.LocalTime;

/** 1. 홈 */
public class HomeScreen extends Page {
    public HomeScreen() { super("home", G.BG, 0, App.H, App.H); }

    @Override public void onShow() { body.repaint(); }

    @Override protected void drawBody(Graphics2D g, Body b) {
        int h = LocalTime.now().getHour();
        String hello = h < 12 ? "좋은 아침이에요" : h < 18 ? "좋은 오후예요" : "좋은 저녁이에요";
        G.text(g, App.userName + "님, " + hello, 40, 34, G.f(14), G.SUB);
        G.text(g, "오늘도 한 문항씩 단단하게", 40, 58, G.b(28), G.INK);

        // 연속 기록 배지
        G.rr(g, 920, 44, 100, 36, 18, G.ORANGE_SOFT);
        G.flame(g, 942, 62, 7, G.ORANGE);
        G.text(g, App.streak + "일 연속", 956, 51, G.b(13.5f), G.ORANGE);
        b.on(920, 44, 100, 36, () -> App.tab("quest"));

        // 면접 시작 카드
        G.rr(g, 40, 120, 560, 220, 20, G.BLUE);
        G.pill(g, "모의 면접", 68, 148, 28, G.b(12.5f), new Color(255, 255, 255, 55), G.WHITE, 11);
        G.arrowRight(g, 548, 162, 18, G.WHITE, 2.4f);
        G.text(g, "면접 시작하기", 68, 210, G.b(28), G.WHITE);
        G.text(g, "면접관 성향을 고르면 AI 면접관이 직접 질문해요.", 68, 260, G.f(14), new Color(0xDCE5FF));
        G.text(g, "내 모습은 녹화해서 피드백해 드려요.", 68, 284, G.f(14), new Color(0xDCE5FF));
        b.on(40, 120, 560, 220, () -> { App.customQuestions = null; App.bossMode = false; App.go("select"); });

        // 오늘의 퀘스트
        G.rr(g, 620, 120, 400, 220, 20, G.WHITE);
        G.text(g, "오늘의 퀘스트", 646, 144, G.b(16), G.INK);
        G.textR(g, "모두 보기", 994, 146, G.b(13), G.BLUE);
        b.on(910, 136, 90, 30, () -> App.tab("quest"));
        Data.Quest[] list = {App.quest("intro"), App.quest("press"), App.quest("gaze")};
        int done = 0;
        for (Data.Quest q : list) if (q.done()) done++;
        G.bar(g, 646, 192, 300, 8, done / 3.0, G.ORANGE, G.TRACK);
        G.textR(g, done + " / 3", 994, 185, G.b(13), G.SLATE);
        for (int i = 0; i < 3; i++) {
            Data.Quest q = list[i];
            double cy = 238 + i * 34;
            if (q.done()) {
                G.circle(g, 657, cy, 11, G.ORANGE);
                G.check(g, 651.5, cy - 4.5, 11, G.WHITE, 2.2f);
                int w = G.text(g, q.title, 678, cy - 10, G.f(14), G.SUB);
                G.line(g, 678, cy + 1, 678 + w, cy + 1, G.SUB, 1f);
            } else {
                G.ring(g, 657, cy, 10.5, G.LINE, 1.7f);
                G.text(g, q.title, 678, cy - 10, G.f(14), G.INK);
            }
            G.textR(g, "+" + q.reward + " XP", 994, cy - 9, G.b(12), G.ORANGE);
        }

        // 최근 면접
        G.text(g, "최근 면접", 40, 372, G.b(17), G.INK);
        G.textR(g, "리포트 보기", 600, 375, G.b(13), G.BLUE);
        b.on(510, 366, 90, 30, () -> { App.lastSession = App.sessions.get(0); App.tab("report"); });
        double y = 410;
        for (int i = 0; i < Math.min(4, App.sessions.size()); i++) {
            Data.Session s = App.sessions.get(i);
            Data.Type t = Data.TYPES[s.type];
            G.rr(g, 40, y, 560, 72, 16, G.WHITE);
            G.rr(g, 56, y + 14, 44, 44, 11, t.tagBg);
            G.textBox(g, t.short1, 56, y + 14, 44, 44, G.b(15), t.tagFg);
            G.text(g, t.name + " · " + s.count + "문항", 116, y + 15, G.b(15), G.INK);
            G.text(g, s.when + " · " + s.modeName(), 116, y + 41, G.f(12.5f), G.SUB);
            G.textR(g, s.total() + "점", 580, y + 22, G.b(19), G.BLUE);
            final Data.Session ss = s;
            b.on(40, y, 560, 72, () -> { App.lastSession = ss; App.go("report"); });
            y += 82;
        }

        // 더 많은 연습
        G.text(g, "더 많은 연습", 620, 372, G.b(17), G.INK);
        String[][] tiles = {
                {"주말 보스전", "깐깐한 임원 3인 패널", "boss"},
                {"채용공고 맞춤 면접", "공고로 예상 질문 만들기", "posting"},
                {"자기소개서 피드백", "고칠 문장 · 살릴 문장", "resume"},
                {"기업 면접 정보", "합격자 후기 · 면접 과정", "company"}};
        Color[] bg = {G.ORANGE_SOFT, G.BLUE_SOFT, G.GREEN_SOFT, G.YELLOW_SOFT};
        Color[] fg = {G.ORANGE, G.BLUE, G.GREEN, new Color(0xB45309)};
        for (int i = 0; i < 4; i++) {
            double tx = 620 + (i % 2) * 204, ty = 410 + (i / 2) * 124;
            G.rr(g, tx, ty, 196, 114, 16, G.WHITE);
            G.circle(g, tx + 32, ty + 32, 14, bg[i]);
            G.circle(g, tx + 32, ty + 32, 6, fg[i]);
            G.text(g, tiles[i][0], tx + 18, ty + 58, G.b(14.5f), G.INK);
            G.text(g, tiles[i][1], tx + 18, ty + 82, G.f(12), G.SUB);
            final String dest = tiles[i][2];
            b.on(tx, ty, 196, 114, () -> App.go(dest));
        }
        b.height((int) Math.max(App.H, y + 30));
    }
}
