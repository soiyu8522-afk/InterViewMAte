package interviewmate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/** 13. 기업 면접 정보 */
public class CompanyScreen extends Page {
    private int tab = 0;
    private boolean unlocked = false;
    private static final Color BROWN = new Color(0x92400E);

    public CompanyScreen() { super("company", G.BG, 100, App.H, 660); }

    @Override protected void drawFixed(Graphics2D g) {
        App.title(g, "기업 면접 정보", "인증된 합격자 후기로 면접 과정과 실제 질문을 확인해요", G.INK, G.SUB);
        String p = App.points + " P";
        int w = G.width(g, p, G.b(15)) + 54;
        G.rr(g, 1020 - w, 34, w, 38, 19, G.YELLOW_SOFT);
        G.coin(g, 1020 - w + 21, 53, BROWN);
        G.text(g, p, 1020 - w + 36, 42, G.b(15), BROWN);
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // ===== 왼쪽: 회사 + 탭 =====
        double x = 40, w = 540;
        G.rr(g, x, 8, w, 104, 18, G.WHITE);
        G.rr(g, x + 22, 30, 60, 60, 14, G.NAVY);
        G.textBox(g, "LOGO", x + 22, 30, 60, 60, G.b(13), G.WHITE);
        G.text(g, "[회사명]", x + 100, 26, G.b(20), G.INK);
        G.text(g, "백엔드 개발자 · 인증된 면접 후기 12개", x + 100, 56, G.f(13), G.SLATE);
        double tx = x + 100 + G.tag(g, "분위기 편안함", x + 100, 80, 22, G.b(12), G.GREEN_SOFT, G.GREEN, 9, 6) + 6;
        G.tag(g, "꼬리질문 많음", tx, 80, 22, G.b(12), G.ORANGE_SOFT, G.ORANGE, 9, 6);

        double y = 128;
        G.rr(g, x, y, w, 46, 12, new Color(0xE5E7EB));
        String[] tabs = {"면접 과정", "실제 질문", "분위기"};
        double cw = (w - 8) / 3;
        for (int i = 0; i < 3; i++) {
            double cx = x + 4 + i * cw;
            if (tab == i) G.rr(g, cx, y + 4, cw, 38, 9, G.WHITE);
            G.textBox(g, tabs[i], cx, y + 4, cw, 38, G.b(14.5f), tab == i ? G.INK : G.SLATE);
            final int k = i;
            b.on(cx, y, cw, 46, () -> tab = k);
        }
        y += 60;
        if (tab == 0) {
            G.rr(g, x, y, w, 260, 18, G.WHITE);
            G.rr(g, x + 32, y + 36, 4, 180, 2, new Color(0xDCE4FB));
            for (int i = 0; i < 4; i++) {
                double cy = y + 36 + i * 60;
                G.circle(g, x + 34, cy, 15, G.BLUE);
                G.textBox(g, String.valueOf(i + 1), x + 19, cy - 15, 30, 30, G.b(13), G.WHITE);
                G.text(g, Data.COMPANY_STEPS[i][0], x + 64, cy - 20, G.b(16), G.INK);
                G.text(g, Data.COMPANY_STEPS[i][1], x + 64, cy + 4, G.f(13.5f), G.SLATE);
            }
            y += 260;
        } else if (tab == 1) {
            double h = Data.COMPANY_QUESTIONS.length * 54 + 20;
            G.rr(g, x, y, w, h, 18, G.WHITE);
            for (int i = 0; i < Data.COMPANY_QUESTIONS.length; i++) {
                double qy = y + 12 + i * 54;
                G.rr(g, x + 14, qy, w - 28, 46, 11, new Color(0xF7F8FA));
                G.text(g, "Q" + (i + 1), x + 30, qy + 14, G.b(13), G.SLATE);
                G.text(g, Data.COMPANY_QUESTIONS[i], x + 62, qy + 14, G.f(14), G.INK);
            }
            y += h;
        } else {
            String[] k = {"편안한 분위기", "꼬리질문 빈도", "압박 강도", "기술 깊이"};
            int[] v = {72, 85, 30, 78};
            G.rr(g, x, y, w, 4 * 48 + 28, 18, G.WHITE);
            for (int i = 0; i < 4; i++) {
                double ry = y + 26 + i * 48;
                G.text(g, k[i], x + 24, ry, G.f(14), G.TEXT);
                G.bar(g, x + 150, ry + 7, 300, 8, v[i] / 100.0, v[i] >= 70 ? G.BLUE : G.ORANGE, G.TRACK);
                G.textR(g, v[i] + "%", x + w - 24, ry, G.b(14), G.INK);
            }
            y += 4 * 48 + 28;
        }
        double leftBottom = y;

        // ===== 오른쪽: 합격자 후기 + 버튼 =====
        double rx = 600, rw = 420, ry = 8;
        String text = unlocked ? Data.REVIEW_FULL : Data.REVIEW_PREVIEW + "○○○○ ○○○ ○○○○ ○○○○…";
        int th = G.split(g, text, rw - 44, G.f(13.5f)).size() * 22;
        double h = 58 + th + (unlocked ? 18 : 70);
        G.rr(g, rx, ry, rw, h, 18, G.WHITE);
        G.rrLine(g, rx, ry, rw, h, 18, new Color(0xFDE68A), 1.4f);
        double tw = G.tag(g, "인증 합격자", rx + 22, ry + 18, 26, G.b(12.5f), G.NAVY, G.WHITE, 10, 7);
        G.text(g, "[시기] 최종 합격 · 상세 후기", rx + 22 + tw + 10, ry + 22, G.f(13), G.SLATE);
        G.wrap(g, text, rx + 22, ry + 58, rw - 44, G.f(13.5f), unlocked ? G.TEXT : new Color(0x9CA3AF), 22);
        if (!unlocked) {
            double by = ry + 58 + th + 12;
            G.rr(g, rx + 22, by, rw - 44, 46, 12, G.YELLOW);
            G.textBox(g, "100P로 전체 후기 열기", rx + 22, by, rw - 44, 46, G.b(15), G.INK);
            b.on(rx + 22, by, rw - 44, 46, () -> {
                if (App.points < 100) { App.toast("포인트가 부족해요. 후기를 쓰고 모아 보세요"); return; }
                App.points -= 100;
                unlocked = true;
                App.toast("100P를 사용해 후기를 열었어요");
            });
        }
        ry += h + 16;
        App.button(g, null, rx, ry, rw, 54, "이 회사 면접관으로 연습", G.BLUE, G.WHITE, null);
        b.on(rx, ry, rw, 54, () -> {
            App.customQuestions = new ArrayList<>(Arrays.asList(Data.COMPANY_QUESTIONS));
            App.bossMode = false;
            App.go("select");
        });
        ry += 64;
        App.button(g, null, rx, ry, rw, 54, "다녀온 면접 후기 쓰고 포인트 받기", G.YELLOW_SOFT, BROWN, null);
        b.on(rx, ry, rw, 54, () -> App.go("review"));
        ry += 54;
        b.height((int) Math.max(leftBottom, ry) + 30);
    }
}
