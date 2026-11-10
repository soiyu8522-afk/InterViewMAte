package interviewmate;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 6. 데일리 퀘스트 */
public class QuestScreen extends Page {
    public QuestScreen() {
        super("quest", G.BG, 100, App.H, 660);
        new javax.swing.Timer(1000, e -> { if (isShowing()) repaint(); }).start();
    }

    @Override public void onShow() { scrollTop(); }

    @Override protected void drawFixed(Graphics2D g) {
        Duration left = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
        App.title(g, "데일리 퀘스트", "매일 자정에 새로 고쳐져요 · 남은 시간 "
                + String.format("%02d:%02d", left.toHours(), left.toMinutes() % 60), G.INK, G.ORANGE);
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // ===== 왼쪽: 연속 기록 =====
        double x = 40, y = 8, w = 460;
        G.rr(g, x, y, w, 230, 20, G.NAVY);
        G.rr(g, x + 24, y + 24, 48, 48, 12, G.ORANGE);
        G.flame(g, x + 48, y + 50, 9, G.WHITE);
        G.text(g, App.streak + "일 연속 연습 중", x + 88, y + 22, G.b(21), G.WHITE);
        G.text(g, "최고 기록 " + App.bestStreak + "일", x + 88, y + 52, G.f(13), new Color(0xD1D5DB));
        G.rr(g, x + w - 82, y + 30, 58, 34, 17, new Color(0x1F2937));
        G.textBox(g, "Lv." + App.level, x + w - 82, y + 30, 58, 34, G.b(14), new Color(0xFDBA74));
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        int today = LocalDate.now().getDayOfWeek().getValue() - 1;
        for (int i = 0; i < 7; i++) {
            double cx = x + 50 + i * 60;
            G.textC(g, days[i], cx, y + 92, G.f(12.5f), new Color(0xD1D5DB));
            if (App.weekDone[i]) {
                G.circle(g, cx, y + 136, 19, G.ORANGE);
                G.check(g, cx - 7.5, y + 130, 15, G.WHITE, 2.4f);
            } else if (i == today) G.ring(g, cx, y + 136, 18, G.ORANGE_BRIGHT, 2f);
            else G.ring(g, cx, y + 136, 18, new Color(0x374151), 2f);
        }
        G.text(g, "다음 레벨까지", x + 24, y + 174, G.f(13), new Color(0xD1D5DB));
        G.textR(g, App.xp + " / 500 XP", x + w - 24, y + 174, G.f(13), new Color(0xD1D5DB));
        G.bar(g, x + 24, y + 200, w - 48, 8, App.xp / 500.0, G.ORANGE_BRIGHT, new Color(0x374151));

        // 주간 챌린지
        double cy = y + 250;
        G.rr(g, x, cy, w, 84, 18, G.BLUE_SOFT);
        G.rr(g, x + 22, cy + 18, 48, 48, 12, G.BLUE);
        G.flag(g, x + 40, cy + 31, 19, G.WHITE, 2f);
        G.text(g, "주간 챌린지 · 5일 연습 (" + App.weeklyDays + "/5)", x + 86, cy + 20, G.b(15), G.INK);
        G.text(g, App.executiveUnlocked ? "깐깐한 임원형 면접관이 열려 있어요" : "완료하면 새 면접관 ‘깐깐한 임원형’이 열려요",
                x + 86, cy + 46, G.f(13), G.SLATE);
        G.bar(g, x + 86, cy + 70, 300, 5, App.weeklyDays / 5.0, G.BLUE, new Color(0xC7D2FE));
        b.on(x, cy, w, 84, () -> App.go("boss"));

        // ===== 오른쪽: 오늘의 퀘스트 =====
        double rx = 520, rw = 500;
        G.text(g, "오늘의 퀘스트", rx, y, G.b(17), G.INK);
        double qy = y + 36;
        for (Data.Quest q : App.quests) {
            G.rr(g, rx, qy, rw, 76, 16, G.WHITE);
            G.rr(g, rx + 16, qy + 15, 46, 46, 11, q.tagBg);
            G.textBox(g, q.tag, rx + 16, qy + 15, 46, 46, G.b(13.5f), q.tagFg);
            G.text(g, q.title, rx + 78, qy + 16, G.b(15), G.INK);
            G.bar(g, rx + 78, qy + 50, 220, 7, q.now / (double) q.goal, G.ORANGE, G.TRACK);
            G.text(g, q.now + "/" + q.goal, rx + 308, qy + 44, G.f(12.5f), G.SUB);
            double bx = rx + rw - 120, by = qy + 18, bw = 104, bh = 40;
            if (q.claimed) {
                G.rr(g, bx, by, bw, bh, 12, new Color(0xF1F2F4));
                G.textBox(g, "받음", bx, by, bw, bh, G.b(14), G.SLATE);
            } else if (q.done()) {
                G.rr(g, bx, by, bw, bh, 12, G.ORANGE);
                G.textBox(g, "+" + q.reward + " XP 받기", bx, by, bw, bh, G.b(13.5f), G.WHITE);
                b.on(bx, by, bw, bh, () -> { q.claimed = true; App.addXp(q.reward); App.toast("+" + q.reward + " XP를 받았어요"); });
            } else {
                G.rr(g, bx, by, bw, bh, 12, G.WHITE);
                G.rrLine(g, bx, by, bw, bh, 12, G.LINE, 1.2f);
                G.textBox(g, "+" + q.reward + " XP", bx, by, bw, bh, G.b(14), G.INK);
                b.on(rx, qy, rw, 76, () -> {
                    if (q.id.equals("press")) App.interviewer = 1;
                    App.customQuestions = null;
                    App.bossMode = false;
                    App.go("select");
                });
            }
            qy += 86;
        }
        b.height((int) Math.max(qy, cy + 84) + 30);
    }
}
