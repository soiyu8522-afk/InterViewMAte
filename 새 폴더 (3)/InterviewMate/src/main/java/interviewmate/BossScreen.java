package interviewmate;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/** 9. 주말 보스전 */
public class BossScreen extends Page {
    private int tab = 0;

    public BossScreen() { super("boss", G.BG, 100, App.H, 660); }

    @Override public void onShow() { scrollTop(); }

    @Override protected void drawFixed(Graphics2D g) {
        App.title(g, "주말 보스전", "일주일에 한 번, 최종 면접처럼 연습해요", G.INK, G.SUB);
        G.tag(g, "일요일 자정 종료", 880, 38, 28, G.b(12.5f), G.ORANGE_SOFT, G.ORANGE, 12, 14);
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // ===== 왼쪽: 이번 주 보스 =====
        double x = 40, y = 8, w = 560, h = 470;
        RoundRectangle2D card = new RoundRectangle2D.Double(x, y, w, h, 40, 40);
        Shape old = g.getClip();
        g.setColor(new Color(0x141A27));
        g.fill(card);
        g.clip(card);
        G.rr(g, x, y, w, 230, 0, new Color(0x201D30));
        g.clip(new Rectangle2D.Double(x, y, w, 230));
        Faces.person(g, x + 120, y + 140, 34, new Color(0x334155), Faces.GRAY_HAIR, Faces.Expr.FROWN, false, false, y + 230);
        Faces.person(g, x + w - 120, y + 140, 34, new Color(0x4A3A2C), Faces.HAIR, Faces.Expr.ANGRY, false, false, y + 230);
        Faces.person(g, x + w / 2, y + 112, 40, new Color(0x0B0F17), Faces.HAIR, Faces.Expr.ANGRY, false, false, y + 230);
        g.setClip(old);
        G.text(g, "이번 주 보스", x + 30, y + 254, G.b(13), new Color(0xFDBA74));
        G.text(g, "깐깐한 임원 3인 패널", x + 30, y + 280, G.b(26), G.WHITE);
        G.wrap(g, "CEO·CTO·인사 임원이 번갈아 묻는 10문항 최종 면접. 돌발 카드가 반드시 2장 나와요.",
                x + 30, y + 328, w - 60, G.f(14), new Color(0xD1D5DB), 22);
        G.text(g, "내 최고 설득 게이지", x + 30, y + 400, G.f(13), new Color(0xD1D5DB));
        int bw = G.width(g, " · 합격선 80%", G.f(13));
        G.textR(g, " · 합격선 80%", x + w - 30, y + 400, G.f(13), new Color(0xD1D5DB));
        G.textR(g, App.bossBest + "%", x + w - 30 - bw, y + 399, G.b(14), G.WHITE);
        G.bar(g, x + 30, y + 430, w - 60, 8, App.bossBest / 100.0, App.bossBest >= 80 ? G.GREEN_BRIGHT : G.ORANGE_BRIGHT, new Color(0x374151));
        double mark = x + 30 + (w - 60) * 0.8;
        G.line(g, mark, y + 424, mark, y + 444, G.WHITE, 1.6f);

        // ===== 오른쪽: 탭 + 보상 + 도전 =====
        double rx = 620, rw = 400;
        G.rr(g, rx, y, rw, 46, 12, new Color(0xE5E7EB));
        String[] tabs = {"클리어 보상", "친구 랭킹"};
        for (int i = 0; i < 2; i++) {
            double tx = rx + 4 + i * (rw - 8) / 2;
            if (tab == i) G.rr(g, tx, y + 4, (rw - 8) / 2, 38, 9, G.WHITE);
            G.textBox(g, tabs[i], tx, y + 4, (rw - 8) / 2, 38, G.b(14.5f), tab == i ? G.INK : G.SLATE);
            final int k = i;
            b.on(tx, y, (rw - 8) / 2, 46, () -> tab = k);
        }
        double ry = y + 60;
        if (tab == 0) {
            String[][] r = {
                    {"배지", "‘최종 면접 통과’ 배지", "프로필에 표시되고 친구에게 공유할 수 있어요"},
                    {"해금", "새 면접관 ‘깐깐한 임원형’", "평소 연습에서도 임원 면접관을 고를 수 있어요"},
                    {"XP", "+500 XP", "첫 클리어 시 한 번 지급돼요"}};
            Color[] bg = {G.ORANGE_SOFT, G.BLUE_SOFT, G.GREEN_SOFT};
            Color[] fg = {G.ORANGE, G.BLUE, G.GREEN};
            for (int i = 0; i < 3; i++) {
                G.rr(g, rx, ry, rw, 76, 16, G.WHITE);
                G.rr(g, rx + 16, ry + 15, 46, 46, 11, bg[i]);
                G.textBox(g, r[i][0], rx + 16, ry + 15, 46, 46, G.b(13.5f), fg[i]);
                G.text(g, r[i][1], rx + 78, ry + 16, G.b(15), G.INK);
                G.text(g, r[i][2], rx + 78, ry + 42, G.f(12.5f), G.SUB);
                if (App.bossCleared) G.tag(g, "획득", rx + rw - 66, ry + 25, 26, G.b(12), G.GREEN_SOFT, G.GREEN, 10, 13);
                ry += 86;
            }
        } else {
            Object[][] rank = {{"민지", 91}, {"준호", 84}, {App.userName + " (나)", App.bossBest}, {"서연", 58}, {"태윤", 47}};
            java.util.Arrays.sort(rank, (a, c) -> (int) c[1] - (int) a[1]);
            G.rr(g, rx, ry, rw, rank.length * 48 + 16, 16, G.WHITE);
            for (int i = 0; i < rank.length; i++) {
                double ly = ry + 8 + i * 48;
                boolean me = ((String) rank[i][0]).contains("(나)");
                if (me) G.rr(g, rx + 8, ly, rw - 16, 44, 10, G.BLUE_SOFT);
                G.textBox(g, String.valueOf(i + 1), rx + 16, ly, 24, 44, G.b(15), i < 3 ? G.ORANGE : G.SLATE);
                G.circle(g, rx + 66, ly + 22, 15, new Color(0xE5E7EB));
                G.textBox(g, ((String) rank[i][0]).substring(0, 1), rx + 51, ly + 7, 30, 30, G.b(12.5f), G.TEXT);
                G.text(g, (String) rank[i][0], rx + 92, ly + 12, G.b(14), G.INK);
                int v = (int) rank[i][1];
                G.textR(g, v + "%", rx + rw - 20, ly + 12, G.b(14.5f), v >= 80 ? G.GREEN : G.SLATE);
            }
            ry += rank.length * 48 + 30;
        }

        // 도전 버튼
        boolean can = App.bossTries > 0;
        double by = Math.max(ry + 6, y + h - 82);
        G.rr(g, rx, by, rw, 56, 14, can ? G.ORANGE : new Color(0x9CA3AF));
        G.textBox(g, can ? "보스전 도전하기" : "이번 주 도전을 모두 썼어요", rx, by, rw, 56, G.b(17), G.WHITE);
        G.textC(g, "이번 주 남은 도전 " + App.bossTries + " / 3회", rx + rw / 2, by + 66, G.f(13), G.SLATE);
        if (can) b.on(rx, by, rw, 56, () -> {
            App.bossTries--;
            App.bossMode = true;
            App.customQuestions = null;
            ((InterviewScreen) App.screen("interview")).start();
            App.go("interview");
        });
        b.height((int) Math.max(y + h, by + 90) + 20);
    }
}
