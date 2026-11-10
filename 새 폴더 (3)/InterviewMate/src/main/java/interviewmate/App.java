package interviewmate;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

/** 면접메이트 – 실행 시작점, 화면 전환, 앱 상태 */
public class App {
    /** 노트북 화면 기준 크기 (모든 좌표의 기준). 왼쪽 SIDE 만큼은 메뉴, 나머지 CW 가 본문 */
    public static final int W = 1280, H = 760, SIDE = 220, CW = W - SIDE;
    /** 화면 배율. 노트북 화면 높이에 맞춰 자동으로 정해지고, Ctrl + / Ctrl - 로 바꿀 수 있음 */
    public static double S = 1;
    public static int px(double v) { return (int) Math.round(v * S); }

    private static JFrame frame;
    private static JPanel root;
    private static final Map<String, Canvas> screens = new HashMap<>();
    private static final Map<String, Supplier<Canvas>> factory = new LinkedHashMap<>();
    private static final Deque<String> history = new ArrayDeque<>();
    private static String current;
    private static Toast toast;

    // ===== 앱 상태 =====
    public static String userName = "지원";
    public static int streak = 7, bestStreak = 12, level = 4, xp = 320, points = 240;
    public static int interviewer = 1;          // Data.TYPES 인덱스
    public static int job = 0, questionCount = 5;
    public static boolean realtime = false;     // false = 녹화 후 피드백
    public static boolean tts = true, saveVideo = true, subtitle = false;
    public static int surpriseFreq = 1;         // 0 없음, 1 가끔, 2 자주
    public static boolean executiveUnlocked = false;
    public static int bossTries = 2, bossBest = 62;
    public static boolean bossCleared = false;
    public static int weeklyDays = 4;
    public static boolean[] weekDone = {true, true, true, false, false, false, false};
    /** 데일리 퀘스트: 진행도, 목표, 보상, 받았는지 */
    public static final Data.Quest[] quests = Data.defaultQuests();
    /** 면접에 쓸 맞춤 질문 (채용공고·자소서에서 넘어온 것). null 이면 성향별 기본 질문 */
    public static List<String> customQuestions = null;
    public static boolean bossMode = false;
    public static final List<Data.Session> sessions = Data.defaultSessions();
    public static Data.Session lastSession = sessions.get(0);
    public static String resumeText = Data.RESUME_DEFAULT;
    public static String postingText = Data.POSTING_DEFAULT;

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(App::start);
    }

    /** 작업 표시줄을 뺀 화면 높이에 창이 딱 들어가는 배율 */
    private static double fitScale() {
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double s = Math.min((screen.width - 20) / (double) W, (screen.height - 50) / (double) H); // 50 = 제목 표시줄 여유
        return Math.max(0.5, Math.min(1.5, s));
    }

    private static void start() {
        S = fitScale();
        register();
        frame = new JFrame("면접메이트");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root = new JPanel(new BorderLayout());
        root.setPreferredSize(new Dimension(px(W), px(H)));
        frame.setContentPane(root);
        toast = new Toast();
        frame.setGlassPane(toast);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        go("home");
        installZoomKeys();
        frame.setVisible(true);
    }

    /** Ctrl + (크게), Ctrl - (작게), Ctrl 0 (화면에 맞춤) */
    private static void installZoomKeys() {
        JRootPane rp = frame.getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();
        int ctrl = java.awt.event.InputEvent.CTRL_DOWN_MASK;
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, ctrl), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, ctrl), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, ctrl), "zoomOut");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, ctrl), "zoomOut");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, ctrl), "zoomFit");
        am.put("zoomIn", new AbstractAction() { public void actionPerformed(java.awt.event.ActionEvent e) { rescale(S + 0.1); } });
        am.put("zoomOut", new AbstractAction() { public void actionPerformed(java.awt.event.ActionEvent e) { rescale(S - 0.1); } });
        am.put("zoomFit", new AbstractAction() { public void actionPerformed(java.awt.event.ActionEvent e) { rescale(fitScale()); } });
    }

    /** 배율을 바꾸고 화면을 새로 만듦 (면접 중에는 바꾸지 않음) */
    public static void rescale(double s) {
        if ("interview".equals(current)) { toast("면접 중에는 크기를 바꿀 수 없어요"); return; }
        S = Math.max(0.5, Math.min(2.0, Math.round(s * 10) / 10.0));
        screens.clear();
        root.setPreferredSize(new Dimension(px(W), px(H)));
        frame.pack();
        show(current);
        toast("화면 크기 " + Math.round(S * 100) + "%");
    }

    public static void register() {
        if (!factory.isEmpty()) return;
        factory.put("home", HomeScreen::new);
        factory.put("select", SelectScreen::new);
        factory.put("feedback", FeedbackModeScreen::new);
        factory.put("interview", InterviewScreen::new);
        factory.put("report", ReportScreen::new);
        factory.put("quest", QuestScreen::new);
        factory.put("thoughts", ThoughtsScreen::new);
        factory.put("boss", BossScreen::new);
        factory.put("posting", PostingScreen::new);
        factory.put("resume", ResumeScreen::new);
        factory.put("expected", ExpectedScreen::new);
        factory.put("company", CompanyScreen::new);
        factory.put("review", ReviewScreen::new);
    }

    /** 화면 이동 */
    public static void go(String name) {
        if (current != null && !current.equals(name)) history.push(current);
        show(name);
    }
    /** 탭 이동 (뒤로가기 기록을 쌓지 않음) */
    public static void tab(String name) { history.clear(); show(name); }
    public static void back() { show(history.isEmpty() ? "home" : history.pop()); }

    private static void show(String name) {
        Canvas s = screens.computeIfAbsent(name, k -> factory.get(k).get());
        current = name;
        // 카메라·마이크는 피드백 설정 화면과 면접 화면에서만 켜 둠
        if (name.equals("feedback") || name.equals("interview")) Media.start(); else Media.stop();
        root.removeAll();
        root.add(s, BorderLayout.CENTER);
        s.onShow();
        root.revalidate();
        root.repaint();
    }
    public static Canvas screen(String name) { return screens.computeIfAbsent(name, k -> factory.get(k).get()); }
    public static JFrame frame() { return frame; }

    public static void toast(String msg) { if (toast != null) toast.show(msg); }

    /** XP 추가 + 레벨업 */
    public static void addXp(int amount) {
        xp += amount;
        while (xp >= 500) { xp -= 500; level++; toast("레벨 업! Lv." + level + " 달성"); }
    }

    /** 퀘스트 진행도 올리기 */
    public static void progress(String id, int amount) {
        for (Data.Quest q : quests) if (q.id.equals(id)) q.now = Math.min(q.goal, q.now + amount);
    }
    public static Data.Quest quest(String id) {
        for (Data.Quest q : quests) if (q.id.equals(id)) return q;
        return null;
    }

    // ===== 왼쪽 메뉴 =====
    private static final String[][] NAV = {
            {"home", "홈"}, {"select", "면접 연습"}, {"quest", "데일리 퀘스트"}, {"report", "면접 리포트"},
            {null, "더 많은 연습"},
            {"boss", "주말 보스전"}, {"posting", "채용공고 맞춤 면접"}, {"resume", "자기소개서 피드백"}, {"company", "기업 면접 정보"}};

    /** 하위 화면이 어느 메뉴에 속하는지 */
    public static String navOf(String screen) {
        switch (screen) {
            case "feedback": return "select";
            case "thoughts": return "report";
            case "expected": return "resume";
            case "review": return "company";
            default: return screen;
        }
    }

    public static void sidebar(Graphics2D g, Canvas c, String screenName) {
        String active = navOf(screenName);
        G.rr(g, 0, 0, SIDE, H, 0, G.WHITE);
        G.line(g, SIDE - 0.5, 0, SIDE - 0.5, H, G.TRACK, 1);
        // 로고
        G.rr(g, 24, 28, 34, 34, 10, G.BLUE);
        G.textBox(g, "면", 24, 28, 34, 34, G.b(16), G.WHITE);
        G.text(g, "면접메이트", 68, 33, G.b(18), G.INK);
        c.hit(16, 20, 190, 50, () -> tab("home"));

        double y = 92;
        Color[] dot = {G.ORANGE, G.BLUE, G.GREEN, new Color(0xB45309)};
        int more = 0;
        for (String[] n : NAV) {
            if (n[0] == null) {
                y += 14;
                G.text(g, n[1], 28, y, G.b(11.5f), G.SUB);
                y += 26;
                continue;
            }
            boolean on = n[0].equals(active);
            if (on) G.rr(g, 14, y, SIDE - 28, 40, 10, G.BLUE_SOFT);
            Color col = on ? G.BLUE : G.TEXT;
            double iy = y + 11;
            switch (n[0]) {
                case "home": G.home(g, 30, iy, 17, col, 1.8f); break;
                case "select": G.video(g, 29, iy, 19, col, 1.8f); break;
                case "quest": G.flag(g, 33, iy, 17, col, 1.8f); break;
                case "report": G.chart(g, 30, iy, 17, col, 1.8f); break;
                default: G.circle(g, 38, y + 20, 5, dot[more++ % 4]);
            }
            G.text(g, n[1], 60, y + 10, on ? G.b(13.5f) : G.f(13.5f), on ? G.BLUE : G.TEXT);
            final String key = n[0];
            c.hit(14, y, SIDE - 28, 40, () -> {
                if (key.equals("select")) { customQuestions = null; bossMode = false; }
                if (key.equals("report")) lastSession = sessions.get(0);
                tab(key);
            });
            y += 44;
        }

        // 내 정보 카드
        double py = H - 132;
        G.rr(g, 14, py, SIDE - 28, 116, 14, G.BG);
        G.circle(g, 46, py + 32, 17, G.BLUE_SOFT);
        G.textBox(g, userName.substring(0, 1), 29, py + 15, 34, 34, G.b(14), G.BLUE);
        G.text(g, userName + "님", 72, py + 14, G.b(14), G.INK);
        G.flame(g, 77, py + 41, 5, G.ORANGE);
        G.text(g, "Lv." + level + " · " + streak + "일 연속", 87, py + 33, G.f(11.5f), G.SLATE);
        G.bar(g, 30, py + 66, SIDE - 60, 6, xp / 500.0, G.ORANGE_BRIGHT, G.TRACK);
        G.text(g, xp + " / 500 XP", 30, py + 80, G.f(11), G.SUB);
        G.textR(g, points + " P", SIDE - 30, py + 80, G.b(11), new Color(0x92400E));
        c.hit(14, py, SIDE - 28, 116, () -> tab("quest"));
    }

    /** 본문 제목 (뒤로가기 없음). 본문 좌표 기준 */
    public static void title(Graphics2D g, String title, String sub, Color ink, Color subCol) {
        G.text(g, title, 40, 30, G.b(25), ink);
        if (sub != null) G.text(g, sub, 40, 68, G.f(13.5f), subCol);
    }

    /** 본문 제목 + 뒤로가기. 본문 좌표 기준 */
    public static void header(Graphics2D g, Canvas c, String title, String sub, Color ink, Color subCol) {
        G.back(g, 42, 38, ink);
        c.hit(28, 24, 40, 44, App::back);
        G.text(g, title, 66, 30, G.b(24), ink);
        if (sub != null) G.text(g, sub, 66, 66, G.f(13.5f), subCol);
    }

    /** 버튼 */
    public static void button(Graphics2D g, Canvas c, double x, double y, double w, double h, String label,
                              Color bg, Color fg, Runnable action) {
        G.rr(g, x, y, w, h, 13, bg);
        G.textBox(g, label, x, y, w, h, G.b(15), fg);
        if (action != null) c.hit(x, y, w, h, action);
    }

    /** 버튼 + 오른쪽 화살표 */
    public static void arrowButton(Graphics2D g, Canvas c, double x, double y, double w, double h, String label,
                                   Color bg, Runnable action) {
        G.rr(g, x, y, w, h, 13, bg);
        int tw = G.width(g, label, G.b(15));
        double tx = x + w / 2 - (tw + 22) / 2.0;
        G.text(g, label, tx, y + h / 2 - 11, G.b(15), G.WHITE);
        G.arrowRight(g, tx + tw + 8, y + h / 2, 12, G.WHITE, 2f);
        c.hit(x, y, w, h, action);
    }

    /** 반투명 안내 메시지 */
    static class Toast extends JComponent {
        private String msg;
        private javax.swing.Timer timer;
        Toast() { setOpaque(false); }
        void show(String m) {
            msg = m;
            setVisible(true);
            repaint();
            if (timer != null) timer.stop();
            timer = new javax.swing.Timer(1700, e -> { msg = null; setVisible(false); });
            timer.setRepeats(false);
            timer.start();
        }
        @Override public boolean contains(int x, int y) { return false; }  // 클릭은 아래 화면으로 통과
        @Override protected void paintComponent(Graphics g0) {
            if (msg == null) return;
            Graphics2D g = (Graphics2D) g0; G.smooth(g);
            Point p = SwingUtilities.convertPoint(root, 0, 0, this);
            g.translate(p.x, p.y);
            g.scale(S, S);
            int w = Math.min(W - 40, G.width(g, msg, G.b(14)) + 40);
            double cx = "interview".equals(current) ? W / 2.0 : SIDE + CW / 2.0;
            G.rr(g, cx - w / 2.0, H - 120, w, 44, 22, new Color(17, 24, 39, 230));
            G.textBox(g, msg, cx - w / 2.0, H - 120, w, 44, G.b(14), G.WHITE);
        }
    }
}
