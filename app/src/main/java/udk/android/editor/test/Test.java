package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import udk.android.pdfeditorlib.dev.R;
import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class Test extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	private boolean pdfReady = false;
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
		pdfView.setOnPDFReadyListener(() -> pdfReady = true);

		// ✅ PDF를 res/raw 에서 바로 오픈
		InputStream is = getResources().openRawResource(R.raw.movietest3);
		pdfView.openPDF(is, 1);

		// 🔹 상단 버튼 코드로 생성
		attachImportButton();
		attachExportButton();

	}//method

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
