package udk.android.editor.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.pdf.PDFEvent;
import udk.android.reader.pdf.PresenterService;
import udk.android.reader.pdf.PresenterService.PresenterServiceListener;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFView.OnViewKeyPreImeListener;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestPresenter extends TestBase {

	private PDFView pdfView;
	private PresenterService psrv;
	private String path;
	private int fullscreen;

	@Override
	public Runnable getAdditionalConfiguration() {
		return new Runnable() {
			@Override
			public void run() {

				LogUtil.DEBUG = true;
				LibConfiguration.DEBUGDRAW = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
			
//				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
//				LibConfiguration.DOUBLE_PAGE_COVER_EXISTS = true;

				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_NONE;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.USE_SMART_NAVIGATION = false;
				LibConfiguration.ENABLE_DIRECT_USER_INPUT = false;

				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR = true;
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.USE_FORM = false;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;

				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = true;

				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;

//				LibConfiguration.EBOOK_MODE_PAGE_FOLDING_WITH_USER_INTERACTION = true;
//				LibConfiguration.EBOOK_MODE_PAGING_WITH_FLING = true;
//				LibConfiguration.NOSCROLL_MODE_PAGING_WITH_FLING = false; 
//				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS_BLOCK_PAGE_OVERSCROL_WITH_CASE_VERTICAL = false;

				LibConfiguration.USE_MULTIPLE_MEDIA_HANDLE = true;
				LibConfiguration.USE_TOP_LAYER_ANNOTATION = true;
				LibConfiguration.USE_INNER_FILEDIR_FOR_PROGRAM_DATA_ROOT = false;
				LibConfiguration.USE_EXTRACT_DATA = true;
				LibConfiguration.USE_MEDIA_EXTERN_RESOURCE = true;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
				LibConfiguration.USE_MEDIA_SUBTITLE_AUTO_SCROLL = true;
				LibConfiguration.USE_FREE_TEXT_ANNOTATION_HINT = true;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_OPTION = false;
				LibConfiguration.USE_ANNOTATION_CREATE_POLYGON = true;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR = true;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_DRAG_DROP = false;

				LibConfiguration.USE_PRESENTER_MODE = true;
			}// method
		};
	}// method

	private boolean toPrev;

	@Override
	public Workable<View> getOnTest() {
		final Context context = this;
		return null;
	}// method

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		path = Environment.getExternalStorageDirectory() + "/LivePage/LivePage_Presenter_v1-Features_Final.pdf";

		Intent intent = getIntent();
		if (intent != null) {
			Uri uri = intent.getData();
			if (uri != null && uri.getScheme().startsWith("file")) {
				path = uri.getPath();
				Log.e("", "path : " + path);
			}
		}

		final Context context = this;
		pdfView = getPDFView();
		pdfView.setFocusable(true);
		pdfView.requestFocus();
		pdfView.setDoublePageViewing(true);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(pdfView.getWindowToken(), 0);

		psrv = pdfView.getPresenterService();
		psrv.setPresenterModeOn(true);
		psrv.setPresenterServiceListener(new PresenterServiceListener() {
			@Override
			public void onPageChanged(PDFEvent e) {
				if (psrv.isPresenterModeOn()) {
					boolean isZoomed = psrv.isZoomed();
					boolean next = e.prevPage < e.page;
					if (isZoomed) {
						psrv.findZoomGroup(next);
//						pdfView.updateFitDirectly(pdfView.getPage());
					} else {
						psrv.findFocus(next);
						pdfView.requestRendering();
					}
				}
			}
		});

		pdfView.setOnViewKeyPreImeListener(new OnViewKeyPreImeListener() {

			@Override
			public boolean onViewKeyPreIme(int keyCode, KeyEvent event) {

//				Log.d("", "onViewKeyPreIme : " + keyCode + ", action: " + event.getAction() + ", downTime: " + event.getDownTime() + ", eventTime: " + event.getEventTime() + ", repeatCount: " + event.getRepeatCount() );
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_ENTER: // focused 선택
					case KeyEvent.KEYCODE_DPAD_CENTER:
						psrv.playZoomedAnnotation();
						break;
					case KeyEvent.KEYCODE_DPAD_LEFT:
						moveFocus(false);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						moveFocus(true);
						break;
					case KeyEvent.KEYCODE_DPAD_UP:
						moveGroup(false);
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						moveGroup(true);
						break;
					case KeyEvent.KEYCODE_ESCAPE:
					case KeyEvent.KEYCODE_BACK:
						if (psrv.isZoomed()) {
							if (psrv.hasParentZoomGroup()) {
								if (!psrv.isZoomed()) {
									pdfView.updateFitDirectly(pdfView.getPage());
								}else {
									psrv.endZoomFocusedAnnotation(true);
								}
							} else {
								psrv.endZoomFocusedAnnotation(true);
							}
						} else {
							finish();
						}
						break;
					case KeyEvent.KEYCODE_M: // 41 메뉴
						break;
					case KeyEvent.KEYCODE_H: // 36 HOME ( 리스트로 )
						finish();
						break;
					case KeyEvent.KEYCODE_T: // 48 TOUCH ( 프리젠터 모드 <-> 터치 모드 )
						psrv.setPresenterModeOn(!psrv.isPresenterModeOn());
						if (psrv.isPresenterModeOn())
							showToast(context, "프리젠터 모드");
						else
							showToast(context, "터치 모드");
						break;
					case KeyEvent.KEYCODE_A: // 29 PEN ( 펜 그리기 <-> 종료 )
						break;
					case KeyEvent.KEYCODE_Z: // 54 ZOOM ( 확대 <-> 축소 )
						if (psrv.isZoomed()) {
							psrv.endZoomFocusedAnnotation(true);
						} else {
							selectFocusedToZoom();
						}
						break;
					case KeyEvent.KEYCODE_S: // 47 Play ( 순서대로 플레이 )
						break;
					case KeyEvent.KEYCODE_R: // 46 멈춤 ( 자동 플레이 멈춤 )
						break;
					case KeyEvent.KEYCODE_F: // 34 FULL ( Image Full -> 양면 -> width fit )
						changeFittingMode();
						break;
					case KeyEvent.KEYCODE_B: // 30 ( 검은 화면 on/off )
						psrv.setBlackScreen(!psrv.isBlackScreen());
						break;
					}
				}
				return true;
			}
		});

		pdfView.openPDF(path, 1);

//		pdfView.openPDF( "/sdcard/test/RxSwift.pdf", 6 );
//		pdfView.openPDF( "/sdcard/test/127-12-Manners-Text_and_Audio-Zoom.pdf", 1 );

	}// method

	private void changeFittingMode() {

		int fittingMode = 0;
		boolean enable = false;

		if (fullscreen++ > 2)
			fullscreen = 0;

		switch (fullscreen) {
		case 0: // Image Full
			fittingMode = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
			enable = false;
			showToast(this, "Image Full");
			break;
		case 1: // 양면
			fittingMode = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
			enable = true;
			showToast(this, "Double page - skip");
			break;
		case 2: // width fit
			fittingMode = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_WIDTHFIT;
			enable = false;
			showToast(this, "Width fit");
			break;
		}

		pdfView.setFittingMode(fittingMode);
//		pdfView.setDoublePageViewing(enable);

		if (psrv.isZoomed()) {
			psrv.endZoomFocusedAnnotation(false);
		}
	}

	private void moveFocus(boolean next) {

		if (psrv.isEmptyPage() || !psrv.findFocus(next) && (!psrv.isZoomed() || !psrv.findZoomGroup(next))) {
			if (next) {
//				pdfView.page(pdfView.getPage()+1);
				pdfView.nextPage();
			} else {
//				if (pdfView.getPage() > 1) {
//					pdfView.page(pdfView.getPage()-1);
//				}
				pdfView.prevPage();
			}
		}
	}

	private void moveGroup(boolean next) {

		if (!psrv.isZoomed() || !psrv.findZoomGroup(next)) {
			if (next) {
				pdfView.nextPage();
			} else {
				pdfView.prevPage();
			}
		}
	}

	private void selectFocusedToZoom() {
		if (psrv.isFocused()) {
			psrv.startZoomFocusedAnnotation();
			psrv.findFocus(true);
		}
	}

	private void showToast(final Context context, final String msg) {
		pdfView.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
}// method
