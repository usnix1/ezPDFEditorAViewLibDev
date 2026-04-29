package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import udk.android.pdfeditorlib.dev.R;
import udk.android.reader.env.LibConfiguration;
import udk.android.reader.pdf.annotation.Annotation;
import udk.android.reader.pdf.annotation.AnnotationAdapter;
import udk.android.reader.pdf.annotation.AnnotationEvent;
import udk.android.reader.pdf.annotation.InkAnnotation;
import udk.android.reader.pdf.annotation.ModifiedCallback;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 *
 * @author JEON YONGTAE
 */
public class Test extends TestBase{
	private static final float TEST_ZOOM_STEP = 1.25f;

	private static class PathHistoryEntry {
		final InkAnnotation annot;
		final ArrayList<double[]> undoPathList;
		final ArrayList<double[]> redoPathList;
		final int undoPage;
		final int redoPage;

		PathHistoryEntry(
				InkAnnotation annot,
				ArrayList<double[]> undoPathList,
				ArrayList<double[]> redoPathList,
				int undoPage,
				int redoPage
		) {
			this.annot = annot;
			this.undoPathList = undoPathList;
			this.redoPathList = redoPathList;
			this.undoPage = undoPage;
			this.redoPage = redoPage;
		}
	}

	private boolean stop;
	private PDFView pdfView;
	private boolean pdfReady = false;
	private boolean applyingUndoRedo = false;
	private final ArrayList<PathHistoryEntry> undoHistory = new ArrayList<PathHistoryEntry>();
	private final ArrayList<PathHistoryEntry> redoHistory = new ArrayList<PathHistoryEntry>();

	private TextView latencyTextView;

	// 마지막 입력 시각
	private volatile long lastFreehandInputTimeMs = 0L;

	// 최근 렌더 반영 latency
	private volatile long lastFreehandLatencyMs = 0L;
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){

				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
				LibConfiguration.ANNOTATION_RENDER_BITMAP_FREEHAND = true;
				LibConfiguration.ANNOTATION_COLOR_FREEHAND = 0x55000055;
				LibConfiguration.ANNOTATION_BORDERWIDTH_FREEHAND = 10;


			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){

		final Context context = this;

		return new Workable< View >(){
			@Override
			public void work( View tool ){

			}//method
		};
	}//method

	@Override
	public void onCreate( Bundle savedInstanceState ){

		super.onCreate( savedInstanceState );

		final Context context = this;


		pdfView = getPDFView();

		// ✅ PDF 로드 완료 콜백
		pdfView.setOnPDFReadyListener(() -> {
			pdfReady = true;

			if (pdfView.okToAddNotes()) {
				pdfView.addAnnotationFreehandStart();
			} else {
				Log.e("PDFTEST", "Annotating not allowed: okToAddNotes() == false");
			}
		});


		// ✅ PDF를 res/raw 에서 바로 오픈
		InputStream is = getResources().openRawResource(R.raw.movietest3);
		pdfView.openPDF(is, 1);

		// 🔹 상단 버튼 코드로 생성
		attachImportButton();
		attachExportButton();
		attachZoomButtons();
		attachInputModeButtons();
		attachUndoRedoButtons();
		attachUndoRedoListener();
//		attachSmoothingToggleButtons();
//		attachLatencyView();
//		pdfView.setOnFreehandLatencyListener(new PDFView.OnFreehandLatencyListener() {
//			@Override
//			public void onFreehandInput(long eventTimeMs) {
//				lastFreehandInputTimeMs = eventTimeMs;
//			}
//
//			@Override
//			public void onFreehandRendered(long renderTimeMs, long latencyMs) {
//				lastFreehandLatencyMs = latencyMs;
//
//				runOnUiThread(() -> {
//					if (latencyTextView != null) {
//						latencyTextView.setText("Latency: " + latencyMs + " ms");
//					}
//				});
//			}
//		});
	}//method

	/**
	 * ===============================
	 * 실시간 Freehand Latency 표시
	 * ===============================
	 */
	private void attachLatencyView() {
		ViewGroup root = findViewById(android.R.id.content);

		latencyTextView = new TextView(this);
		latencyTextView.setText("Latency: - ms");
		latencyTextView.setTextSize(14f);
		latencyTextView.setPadding(dp(10), dp(6), dp(10), dp(6));
		latencyTextView.setBackgroundColor(0xAA000000);
		latencyTextView.setTextColor(0xFFFFFFFF);

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lp.gravity = Gravity.TOP | Gravity.START;
		lp.topMargin = dp(12);
		lp.leftMargin = dp(12);

		latencyTextView.setLayoutParams(lp);
		root.addView(latencyTextView);
	}

	/**
	 * ===============================
	 * Freehand Smoothing ON/OFF 버튼
	 * ===============================
	 */
	private void attachSmoothingToggleButtons() {
		ViewGroup root = findViewById(android.R.id.content);

		// ===============================
		// ON 버튼
		// ===============================
		Button buttonSmoothOn = new Button(this);
		buttonSmoothOn.setText("Smooth ON");

		FrameLayout.LayoutParams lpOn = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpOn.gravity = Gravity.TOP | Gravity.END;
		lpOn.topMargin = dp(60);      // Export 아래
		lpOn.rightMargin = dp(120);   // Export와 맞춤

		buttonSmoothOn.setLayoutParams(lpOn);

		buttonSmoothOn.setOnClickListener(v -> {
//			pdfView.setFreehandSmoothingEnabled(true);

			Toast.makeText(
					this,
					"Smoothing ON",
					Toast.LENGTH_SHORT
			).show();
		});

		root.addView(buttonSmoothOn);


		// ===============================
		// OFF 버튼
		// ===============================
		Button buttonSmoothOff = new Button(this);
		buttonSmoothOff.setText("Smooth OFF");

		FrameLayout.LayoutParams lpOff = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpOff.gravity = Gravity.TOP | Gravity.END;
		lpOff.topMargin = dp(110);     // ON 버튼 아래
		lpOff.rightMargin = dp(120);

		buttonSmoothOff.setLayoutParams(lpOff);

		buttonSmoothOff.setOnClickListener(v -> {
//			pdfView.setFreehandSmoothingEnabled(false);

			Toast.makeText(
					this,
					"Smoothing OFF",
					Toast.LENGTH_SHORT
			).show();
		});

		root.addView(buttonSmoothOff);
	}

	/**
	 * ===============================
	 * 상단 XFDF Import 버튼 (코드 생성)
	 * ===============================
	 */
	private void attachImportButton() {
		// 루트 뷰 (PDFView가 포함된 최상위)
		ViewGroup root = findViewById(android.R.id.content);

		// 버튼 생성
		Button btn = new Button(this);
		btn.setText("XFDF Import");

		// 레이아웃 파라미터 (상단 고정)
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lp.gravity = Gravity.TOP | Gravity.END;
		lp.topMargin = dp(12);
		lp.rightMargin = dp(12);

		btn.setLayoutParams(lp);

		// 클릭 → XFDF import
		btn.setOnClickListener(v -> {
			importXfdfFromRaw(R.raw.annot_2345);
		});

		// 화면에 추가
		root.addView(btn);
	}

	/**
	 * ===============================
	 * 상단 XFDF Export 버튼
	 * ===============================
	 */
	private void attachExportButton() {
		// 루트 뷰 (PDFView가 포함된 최상위)
		ViewGroup root = findViewById(android.R.id.content);

		// 버튼 생성
		Button btn = new Button(this);
		btn.setText("XFDF Export");

		// 레이아웃 파라미터 (상단 고정)
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lp.gravity = Gravity.TOP | Gravity.END;
		lp.topMargin = dp(12);
		lp.rightMargin = dp(120); // Import 버튼과 간격

		btn.setLayoutParams(lp);

		// 클릭 → PDFView.exportToXFDFLarge 호출
		btn.setOnClickListener(v -> {
			try {
				File out = new File(
						getExternalFilesDir(null),
						"exported_annotations.xfdf"
				);

				LogUtil.d(out.getAbsolutePath());
				// ✅ PDFView에 만든 함수 호출
				pdfView.exportToXFDFLarge(out.getAbsolutePath());

				Toast.makeText(
						this,
						"XFDF Export 완료\n" + out.getAbsolutePath(),
						Toast.LENGTH_LONG
				).show();

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(
						this,
						"XFDF Export 실패",
						Toast.LENGTH_SHORT
				).show();
			}
		});

		// 화면에 추가
		root.addView(btn);
	}

	private void attachZoomButtons() {
		ViewGroup root = findViewById(android.R.id.content);

		Button zoomInButton = new Button(this);
		zoomInButton.setText("Zoom +");

		FrameLayout.LayoutParams lpIn = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpIn.gravity = Gravity.TOP | Gravity.END;
		lpIn.topMargin = dp(60);
		lpIn.rightMargin = dp(12);
		zoomInButton.setLayoutParams(lpIn);
		zoomInButton.setOnClickListener(v -> runWhenPdfReady(() -> adjustZoom(TEST_ZOOM_STEP)));
		root.addView(zoomInButton);

		Button zoomOutButton = new Button(this);
		zoomOutButton.setText("Zoom -");

		FrameLayout.LayoutParams lpOut = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpOut.gravity = Gravity.TOP | Gravity.END;
		lpOut.topMargin = dp(60);
		lpOut.rightMargin = dp(120);
		zoomOutButton.setLayoutParams(lpOut);
		zoomOutButton.setOnClickListener(v -> runWhenPdfReady(() -> adjustZoom(1.0f / TEST_ZOOM_STEP)));
		root.addView(zoomOutButton);
	}

	private void attachInputModeButtons() {
		ViewGroup root = findViewById(android.R.id.content);

		Button freehandButton = new Button(this);
		freehandButton.setText("Freehand");

		FrameLayout.LayoutParams lpFreehand = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpFreehand.gravity = Gravity.TOP | Gravity.END;
		lpFreehand.topMargin = dp(156);
		lpFreehand.rightMargin = dp(120);
		freehandButton.setLayoutParams(lpFreehand);
		freehandButton.setOnClickListener(v -> runWhenPdfReady(this::activateFreehandMode));
		root.addView(freehandButton);

		Button eraserButton = new Button(this);
		eraserButton.setText("Eraser");

		FrameLayout.LayoutParams lpEraser = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpEraser.gravity = Gravity.TOP | Gravity.END;
		lpEraser.topMargin = dp(156);
		lpEraser.rightMargin = dp(12);
		eraserButton.setLayoutParams(lpEraser);
		eraserButton.setOnClickListener(v -> runWhenPdfReady(this::activateEraserMode));
		root.addView(eraserButton);
	}

	private void activateFreehandMode() {
		if (!pdfView.okToAddNotes()) {
			Toast.makeText(this, "Freehand not allowed", Toast.LENGTH_SHORT).show();
			return;
		}
		pdfView.addAnnotationFreehandStart();
	}

	private void activateEraserMode() {
		pdfView.clearAnnotationStart();
	}

	private void attachUndoRedoButtons() {
		ViewGroup root = findViewById(android.R.id.content);

		Button undoButton = new Button(this);
		undoButton.setText("Undo");

		FrameLayout.LayoutParams lpUndo = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpUndo.gravity = Gravity.TOP | Gravity.END;
		lpUndo.topMargin = dp(108);
		lpUndo.rightMargin = dp(120);
		undoButton.setLayoutParams(lpUndo);
		undoButton.setOnClickListener(v -> runWhenPdfReady(this::performUndo));
		root.addView(undoButton);

		Button redoButton = new Button(this);
		redoButton.setText("Redo");

		FrameLayout.LayoutParams lpRedo = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		lpRedo.gravity = Gravity.TOP | Gravity.END;
		lpRedo.topMargin = dp(108);
		lpRedo.rightMargin = dp(12);
		redoButton.setLayoutParams(lpRedo);
		redoButton.setOnClickListener(v -> runWhenPdfReady(this::performRedo));
		root.addView(redoButton);
	}

	private void attachUndoRedoListener() {
		pdfView.getAnnotationService().addListener(new AnnotationAdapter() {
			@Override
			public void onModifiedCallack(ModifiedCallback callback) {
				if (applyingUndoRedo
						|| callback == null
						|| callback.type != ModifiedCallback.ModifiedCallbackType.ANNOT_PATHS
						|| !(callback.target instanceof InkAnnotation)
						|| callback.oldPathList == null) {
					return;
				}

				InkAnnotation ink = (InkAnnotation) callback.target;
				undoHistory.add(new PathHistoryEntry(
						ink,
						copyPathList(callback.oldPathList),
						copyPathList(ink.getPathList()),
						callback.oldPage,
						ink.getPage()
				));
				redoHistory.clear();
			}

			@Override
			public void onAnnotationRemoved(AnnotationEvent e) {
				if (e == null || e.current == null) {
					return;
				}
				removeHistoryFor(e.current);
			}
		});
	}

	private void performUndo() {
		if (pdfView.isNowCreatingAnnotationFreehand()) {
			if (pdfView.addAnnotationFreehandCanUndo()) {
				pdfView.addAnnotationFreehandUndo();
			} else {
				Toast.makeText(this, "Current freehand undo empty", Toast.LENGTH_SHORT).show();
			}
			return;
		}

		if (undoHistory.isEmpty()) {
			Toast.makeText(this, "Undo history empty", Toast.LENGTH_SHORT).show();
			return;
		}

		PathHistoryEntry entry = undoHistory.remove(undoHistory.size() - 1);
		if (applyPathHistory(entry, true)) {
			redoHistory.add(entry);
		}
	}

	private void performRedo() {
		if (pdfView.isNowCreatingAnnotationFreehand()) {
			if (pdfView.addAnnotationFreehandCanRedo()) {
				pdfView.addAnnotationFreehandRedo();
			} else {
				Toast.makeText(this, "Current freehand redo empty", Toast.LENGTH_SHORT).show();
			}
			return;
		}

		if (redoHistory.isEmpty()) {
			Toast.makeText(this, "Redo history empty", Toast.LENGTH_SHORT).show();
			return;
		}

		PathHistoryEntry entry = redoHistory.remove(redoHistory.size() - 1);
		if (applyPathHistory(entry, false)) {
			undoHistory.add(entry);
		}
	}

	private boolean applyPathHistory(PathHistoryEntry entry, boolean undo) {
		if (entry == null || entry.annot == null) {
			return false;
		}

		ArrayList<double[]> targetPathList = undo ? entry.undoPathList : entry.redoPathList;
		int targetPage = undo ? entry.undoPage : entry.redoPage;
		if (targetPathList == null) {
			return false;
		}

		applyingUndoRedo = true;
		try {
			pdfView.getPDF().updateInkAnnotationPoints(entry.annot, copyPathList(targetPathList));
			if (entry.annot.getPage() != targetPage) {
				pdfView.getAnnotationService().updateAnnotationPage(entry.annot, targetPage);
			}
			pdfView.getAnnotationService().updateFinalizeAnnotationTransformAdj(entry.annot);
			return true;
		} finally {
			applyingUndoRedo = false;
		}
	}

	private void removeHistoryFor(Annotation annotation) {
		if (annotation == null) {
			return;
		}
		removeHistoryFor(undoHistory, annotation);
		removeHistoryFor(redoHistory, annotation);
	}

	private void removeHistoryFor(List<PathHistoryEntry> history, Annotation annotation) {
		for (int i = history.size() - 1; i >= 0; i--) {
			PathHistoryEntry entry = history.get(i);
			if (entry != null && entry.annot == annotation) {
				history.remove(i);
			}
		}
	}

	private ArrayList<double[]> copyPathList(List<double[]> source) {
		ArrayList<double[]> copy = new ArrayList<double[]>();
		if (source == null) {
			return copy;
		}
		for (double[] path : source) {
			if (path == null) {
				copy.add(null);
			} else {
				copy.add(path.clone());
			}
		}
		return copy;
	}

	private void adjustZoom(float ratio) {
		float nextZoom = pdfView.getZoom() * ratio;
		if (nextZoom < LibConfiguration.ZOOM_MIN) {
			nextZoom = LibConfiguration.ZOOM_MIN;
		} else if (nextZoom > LibConfiguration.ZOOM_MAX) {
			nextZoom = LibConfiguration.ZOOM_MAX;
		}
		pdfView.zoomTo(nextZoom);
	}

	/**
	 * ===============================
	 * XFDF import (res/raw)
	 * ===============================
	 */
	private void importXfdfFromRaw(int rawResId) {
		runWhenPdfReady(() -> {
			try (InputStream is = getResources().openRawResource(rawResId)) {
//				pdfView.importXFDF(is);
				pdfView.importXFDFLarge(is);
				pdfView.invalidate();
				Toast.makeText(
						this,
						"XFDF Import 완료\n" + rawResId,
						Toast.LENGTH_LONG
				).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(
						this,
						"XFDF Export 실패",
						Toast.LENGTH_SHORT
				).show();
			}
		});
	}

	/**
	 * ===============================
	 * PDF 로딩 완료 후 실행 보장
	 * ===============================
	 */
	private void runWhenPdfReady(Runnable task) {
		if (pdfReady) {
			task.run();
		} else {
			pdfView.post(() -> runWhenPdfReady(task));
		}
	}

	/**
	 * dp → px
	 */
	private int dp(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}
}//method
