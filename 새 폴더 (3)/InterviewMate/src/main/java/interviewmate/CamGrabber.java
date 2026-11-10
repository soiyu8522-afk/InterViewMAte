package interviewmate;

import com.github.sarxos.webcam.Webcam;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * 웹캠 라이브러리(webcam-capture)를 직접 쓰는 유일한 클래스.
 * 라이브러리가 없으면 이 클래스를 불러올 때 오류가 나고, Media 가 그 오류를 잡아서 그림(실루엣)으로 대신합니다.
 */
class CamGrabber {
    private Webcam cam;

    boolean open() {
        cam = Webcam.getDefault();
        if (cam == null) return false;
        // 640 x 480 이하 중에서 가장 큰 해상도 고르기
        Dimension best = null;
        for (Dimension d : cam.getViewSizes()) {
            if (d.width <= 640 && (best == null || d.width > best.width)) best = d;
        }
        if (best != null) cam.setViewSize(best);
        return cam.open();
    }

    BufferedImage grab() { return cam == null ? null : cam.getImage(); }

    void close() { if (cam != null && cam.isOpen()) cam.close(); }
}
