	package udk.android.editor.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import udk.android.pdfeditorlib.dev.R;
import udk.android.reader.env.LibConfiguration;
import udk.android.reader.env.LibLog;
import udk.android.reader.pdf.PDFException;
import udk.android.reader.pdf.TextParagraph;
import udk.android.reader.pdf.annotation.Annotation;
import udk.android.reader.pdf.annotation.AnnotationEvent;
import udk.android.reader.pdf.annotation.AnnotationListener;
import udk.android.reader.pdf.annotation.InkAnnotation;
import udk.android.reader.pdf.annotation.ModifiedCallback;
import udk.android.reader.view.pdf.GlobalConfigurationService;
import udk.android.reader.view.pdf.PDFReadingService;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFViewEvent;
import udk.android.reader.view.pdf.PDFViewListener;
import udk.android.pdfeditorlib.dev.BuildConfig;
import udk.android.reader.view.pdf.PDFViewListenerEx;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.util.vo.menu.ToolMenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestQuiz extends TestBase implements AnnotationListener{
	
	private PDFView pdfView;

	ModifiedCallback lastCallback;

	Annotation ca;
	Annotation ra;

	public void startLineAnnot() {
		LibConfiguration.ANNOTATION_BORDERWIDTH_FIGURE = 3;
		LibConfiguration.ANNOTATION_COLOR_FIGURE = 0xffff8800;
		LibConfiguration.ANNOTATION_BORDERSTYLE = Annotation.BORDER_DASHED;
//		LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{1,3};
//		LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{4,3};
		LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{6,3,2,3};
		LibConfiguration.ANNOTATION_COLOR_FIGURE_INNER_EXISTS = false;
		pdfView.addAnnotationArrowStart();
	}
	public void startInkAnnot() {
		pdfView.addAnnotationFreehandStart();
	}
	public void endAnnot() {
//		pdfView.getInteractionService().setDragListener(null);
		pdfView.addAnnotationFreehandEndConfirm();
	}
	public void createAnnot() {
		Annotation annotation = ra;
		if ( annotation instanceof InkAnnotation ) {
			InkAnnotation ink = (InkAnnotation)annotation;
			pdfView.getAnnotationService().registAnnotationToCacheList(ink);
			pdfView.addAnnotationFreehandEndConfirm(ink, null);
		}
	}
	public void removeAnnot() {
		Annotation annotation = ca;
		if (annotation != null) {
			pdfView.getAnnotationService().removeAnnotation(annotation);
		}
	}

	public void moveAnnot() {
		Annotation annotation = lastCallback.target;
		if (annotation != null) {
			InkAnnotation ink = (InkAnnotation)annotation;
//			ink.setPathList(lastCallback.oldPathList);
			pdfView.getPDF().updateInkAnnotationPoints(ink, lastCallback.oldPathList);
			AnnotationEvent e = new AnnotationEvent();
			e.current = ink;
			pdfView.getAnnotationService().fireAnnotationUpdated(e);
			pdfView.getAnnotationService().fireAnnotationAppearenceChanged(e);
		}
	}



	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				LibLog.INFOLOG = true;
				LibLog.INFONATIVELOG = true;
				LogUtil.DEBUG = true;
				//LibConfiguration.DEBUGDRAW = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_COVER_EXISTS = false;

				LibConfiguration.ZOOM_MAX = 16.0f;
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_NONE;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = true;
				LibConfiguration.USE_ANNOTATION_EFFCET_CLOUDY = true;


				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.ENABLE_DIRECT_USER_INPUT = true;
				LibConfiguration.ENABLE_FORMFIELD_TAP_ACTION = true;

				/* Rendering Part */
//				LibConfiguration.TILESIZE_RATIO_FOR_DISPLAYRESOLUTION = 0.3f;
//				LibConfiguration.LQ_PRERENDER = false;
//				LibConfiguration.TRY_FIND_CACHED_TILE = false;
//				LibConfiguration.CACHING_CURRENT_BASIC_ONLY = true;

//				LibConfiguration.EBOOK_MODE_PAGE_FOLDING_WITH_USER_INTERACTION = true;
//				LibConfiguration.EBOOK_MODE_PAGING_WITH_FLING = true;
//				LibConfiguration.NOSCROLL_MODE_PAGING_WITH_FLING = false;
//				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS_BLOCK_PAGE_OVERSCROL_WITH_CASE_VERTICAL = false;

				LibConfiguration.USE_MULTIPLE_MEDIA_HANDLE = true;
				LibConfiguration.USE_TOP_LAYER_ANNOTATION = false;
//				LibConfiguration.USE_INNER_FILEDIR_FOR_PROGRAM_DATA_ROOT = false;
				LibConfiguration.USE_EXTRACT_DATA = true;
				LibConfiguration.USE_MEDIA_EXTERN_RESOURCE = true;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
				LibConfiguration.USE_MEDIA_SUBTITLE_AUTO_SCROLL = true;
				LibConfiguration.USE_FREE_TEXT_ANNOTATION_HINT = true;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_OPTION = false;
				LibConfiguration.USE_ANNOTATION_CREATE_POLYGON = true;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR = true;
//				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_DRAG_DROP = false;

				LibConfiguration.USE_IN_PDF_LEFT_TOP_ALIGN = true;
				LibConfiguration.USE_SYNC_CONCURRENT_ANIMATION_OBJECT = true;

				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_VIDEO = false;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_SOUND = false;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_TEXT_HIGHLIGHT = false;

				LibConfiguration.USE_ANNOTATION_CREATE_REDACT = true;
				LibConfiguration.USE_JAVASCRIPT = true;

				LibConfiguration.USE_ANNOTATION_CREATE_POLYGON_MARKX = true;
				LibConfiguration.USE_ANNOTATION_CREATE_MEASURE = true;

				LibConfiguration.USE_ANNOTATION_CONTEXTMENU_PROPERTIES = true;

				LibConfiguration.USE_SYNC_CONCURRENT_ANIMATION_OBJECT = true;

				LibConfiguration.USE_EZPDFDRM_SKIP_REFERENCE = true;

				LibConfiguration.USE_FORMFIELD_SAME_TITLE = true;
				LibConfiguration.POSTFIX_FORMFIELD_IGNORE = "__";
				LibConfiguration.USE_EXPORT_TOTAL_PAGE = true;
				LibConfiguration.USE_EXPORT_CDATA_ONOFF_IMAGE = true;
				LibConfiguration.USE_EXPORT_EMPTY_VALUE_FOR_NONE = true;
//				LibConfiguration.USE_SINGLE_EACH_MEDIA_HANDLE = true;

				LibConfiguration.USE_EXPORT_CDATA_IMAGE = true;

				LibConfiguration.USE_ANNOTATION_CREATE_IMAGE_EXTRA_PNG = true;
				LibConfiguration.XFDF_STAMP_INCLUDE_IMAGE_DATA = true;

				LibConfiguration.BETA_FREEHAND_DRAWING_CALLBACK_IN_DOUBLEPAGE_VIEWING = true;

				GlobalConfigurationService gs = GlobalConfigurationService.getInstance();
				gs.setEbookMode(false);
				gs.setContinuousScrollMode(1);
				gs.setContinuousScrollAutoPageInterval(true);
				gs.setCacheEnabled(false);
				gs.setFittingMode(3);

//				LibConfiguration.USE_TTS = true;
			}//method
		};
	}//method

	public String ReadTextFile(String path){
		StringBuffer strBuffer = new StringBuffer();
		try{
			FileInputStream is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line="";
			while((line=reader.readLine())!=null){
				strBuffer.append(line+"\n");
			}

			reader.close();
			is.close();
		}catch ( Exception e){
			e.printStackTrace();
			return "";
		}
		return strBuffer.toString();
	}

	private boolean toPrev;
	private ToolMenuCommand tc;

	private byte[] byteXfdf = null;
	@Override
	public Workable< View > getOnTest(){
		final Context context = this;
		return new Workable< View >(){
			@Override
			public void work( View tool ){

				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "테스트1 - create", new Runnable(){
					@Override
					public void run(){
						//RectF rectF = pdfView.getPageBounds();
						//pdfView.addAnnotationFreehandStart(true, rectF); // pageBounds에만 필기 주석 시작
						//pdfView.addAnnotationTextBoxStart();
						pdfView.addAnnotationFreehandStart();
						//pdfView.addAnnotationRectangleStart();
						//pdfView.addAnnotationLineStart();
						//pdfView.addAnnotationOvalStart();
						//pdfView.addAnnotationPolygonStart(5);
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - end", new Runnable(){
					@Override
					public void run(){

						if( pdfView.addAnnotationFreehandCanUndo()){
							pdfView.addAnnotationFreehandEndConfirm();
						} else {
							pdfView.addAnnotationFreehandEndCancel();
						}

					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - clear start", new Runnable(){
					@Override
					public void run(){
						pdfView.clearAnnotationStart();
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - clear end", new Runnable(){
					@Override
					public void run(){
						pdfView.clearAnnotationEnd();
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - flatten", new Runnable(){
					@Override
					public void run(){
						try {
							pdfView.flatten();
						} catch (PDFException e) {
							e.printStackTrace();
						}
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트2 - delete", new Runnable(){
					@Override
					public void run(){
						pdfView.deleteAllAnnotation();
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트2- rotate", new Runnable(){
					@Override
					public void run(){
						int rotate = pdfView.getCurrentPageRotate() + 90;
						pdfView.rotateCurrentPage(rotate);
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트2- save", new Runnable(){
					@Override
					public void run(){
						pdfView.save();
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트3 - export", new Runnable(){
					@Override
					public void run()
					{
						try {
							StringWriter value = new StringWriter();
							pdfView.exportXFDF(value);
							byteXfdf = value.toString().getBytes(StandardCharsets.UTF_8);

						} catch (Exception e ){

						}

					}//method
				} ) );

				mcs.add( new MenuCommand( "테스트4 - import", new Runnable(){
					@Override
					public void run(){
						if( byteXfdf == null )
							byteXfdf = readFromFile("/sdcard/test.xfdf");

						if( byteXfdf != null && byteXfdf.length > 0){
							try {
								pdfView.importXFDF(byteXfdf);
							} catch (Exception e){

							}

							byteXfdf = null;
						}

					}
				} ) );

				mcs.add( new MenuCommand( "이전페이지", new Runnable(){
					@Override
					public void run(){
						pdfView.prevPage();
					}
				} ) );
				mcs.add( new MenuCommand( "다음페이지", new Runnable(){
					@Override
					public void run(){
						pdfView.nextPage();
					}
				} ) );
				mcs.add( new MenuCommand( "페이지 리셋", new Runnable(){
					@Override
					public void run(){
						pdfView.resetPage(pdfView.getPage());
					}
				} ) );
				WidgetFactory.uiPopupMenu( tool, mcs );

			}//method
		};
	}//method

	private void BitmapConvertFile(Bitmap bitmap, String strFilePath)
	{
		// 파일 선언 -> 경로는 파라미터에서 받는다
		File file = new File(strFilePath);

		// OutputStream 선언 -> bitmap데이터를 OutputStream에 받아 File에 넣어주는 용도
		OutputStream out = null;
		try {
			// 파일 초기화
			file.createNewFile();

			// OutputStream에 출력될 Stream에 파일을 넣어준다
			out = new FileOutputStream(file);

			// bitmap 압축
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				out.flush();
				out.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] readFromFile(String path){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				for (int readNum; (readNum = fis.read(b)) != -1; ) {
					bos.write(b, 0, readNum);
				}
				return bos.toByteArray();
			} catch (Exception e) {

			}
			return null;
	}

	MediaSubtitleService ms;
	private View bg;
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;

		pdfView = getPDFView();
//		ImageView bg = new ImageView(this);
//		bg = new View( this){
//			private Paint paint;
//
//			@Override
//			protected void onDraw(Canvas canvas) {
//				super.onDraw(canvas);
//				if( paint == null ) {
//					paint = new Paint();
//					paint.setStyle(Paint.Style.STROKE);
//					paint.setColor(Color.RED);
//					paint.setStrokeWidth(5);
//				}
//				if( pdfView != null ) {
//					RectF temp = pdfView.convertPageBoundsToViewBounds(new RectF(100, 100, 500, 500));
//					canvas.drawRect(temp, paint);
//				}
//			}
//		};
//		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//		getParentContainer().addView(bg, rl);


//		ms= new MediaSubtitleService( pdfView, bg );
//		pdfView.setOnPDFReadyListener(new PDFView.OnPDFReadyListener() {
//			@Override
//			public void onPDFReady() {
//				ms.init();
//			}
//		});

		if(Build.VERSION.SDK_INT >= 30){
			if( !Environment.isExternalStorageManager() ) {
				try {
					Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
					Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
					startActivity(intent);
				} catch (Exception ex) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
					startActivity(intent);
				}
			} else {
				openPDF();
			}
		} else {
			openPDF();
		}




	}//method

	private static String GetAppID(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);

			byte[] cert = info.signatures[0].toByteArray();
			InputStream input = new ByteArrayInputStream(cert);

			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate c = (X509Certificate) cf.generateCertificate(input);

			MessageDigest md = MessageDigest.getInstance("SHA1");

			String rpfacetinfo = "android:apk-key-hash:"
					+ Base64.encodeToString(md.digest(c.getEncoded()),
					Base64.DEFAULT | Base64.NO_WRAP | Base64.NO_PADDING);

			return rpfacetinfo;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void openPDF() {

		pdfView.setPDFViewListener(new PDFViewListener() {
			@Override
			public void onOpenCompleted(PDFViewEvent e) {

			}

			@Override
			public void onSavedAs(PDFViewEvent e) {

			}

			@Override
			public void onPageChanged(PDFViewEvent e) {

			}

			@Override
			public void onZoomChanged(PDFViewEvent e) {

			}

			@Override
			public void onSingleTapUp(PDFViewEvent e) {
			}

			@Override
			public void onDoubleTapUp(PDFViewEvent e) {

			}

			@Override
			public void onLongPress(PDFViewEvent e) {

			}

			@Override
			public void onLinkTapped(PDFViewEvent e) {

			}

			@Override
			public void onColumnFitActivated(PDFViewEvent e) {

			}

			@Override
			public void onColumnFitDeactivated(PDFViewEvent e) {

			}

			@Override
			public void onViewUpdated(PDFViewEvent e) {

			}
		});

		pdfView.setOnAnnotationFreehandCreatingListener(new PDFView.OnAnnotationFreehandCreatingListener() {
			@Override
			public void onStart(int page, String uid) {

			}

			@Override
			public void onNewLineStart() {

			}

			@Override
			public void onNewLine(String xfdf) {
				if( pdfView.addAnnotationFreehandCanUndo()){
					pdfView.addAnnotationFreehandEndConfirm();
				} else {
					pdfView.addAnnotationFreehandEndCancel();
				}
			}

			@Override
			public void onUndo(String xfdf) {

			}

			@Override
			public void onRedo(String xfdf) {

			}

			@Override
			public void onEndConfirm(String xfdf) {
				pdfView.addAnnotationFreehandStart();
			}

			@Override
			public void onEndCancel(int page, String uid) {

			}
		});

		/*
		pdfView.setPDFViewListenerEx(new PDFViewListenerEx() {
			@Override
			public void onPageChangeTried(PDFViewEvent e) {
				if(pdfView.addAnnotationFreehandCanUndo()) {
					pdfView.addAnnotationFreehandEndConfirm(new Runnable() {
						@Override
						public void run() {
							pdfView.addAnnotationFreehandStart();
						}
					});
				} else
					pdfView.addAnnotationFreehandEndCancel();
			}
		});

		pdfView.setOnViewPageChangeListener(new PDFView.OnViewPageChangeListener() {
			@Override
			public void onViewPageChange(int page) {

			}
		});
		 */
		pdfView.getAnnotationService().addListener(this);
		pdfView.setContinuousAudioPlayMode(true);

		try {



/*
			ExtraOpenOptions eoos = new ExtraOpenOptions();
			eoos.encryptedDrmParamExtraExtern = "token=" + Base64.encodeToString(strLoginToken.getBytes(), Base64.NO_WRAP);
			eoos.encryptedDrmFileSavePath = getCacheDir().getAbsolutePath();
*/
			//pdfView.openPDF( "/sdcard/test/ezPDF Webviewer_매뉴얼.pdf", "abcd123!", "abcd123!", 0, 1, true, null );
			///pdfView.openPDF("/sdcard/test/ezPDF Webviewer_매뉴얼.pdf", "abcd123!", "abcd123!", 1, 1, true, null, null);
			pdfView.openPDF("/sdcard/sample.pdf", 1 );

//			pdfView.openPDF(getAssets().open("C01_4.PDF"), 0);
		} catch ( Exception e ){

		}
//		pdfView.openPDF( "http://stage.ibk.co.kr/fup/customer/form/2021121015525472454931559495957.pdf", 1 );
		//pdfView.openPDF( "/sdcard/test/1334067_카티아 도움닫기.pdf", 1 );
//		pdfView.openPDF( "https://manuals.info.apple.com/MANUALS/1000/MA1595/en_US/ipad_user_guide.pdf?filename=ipad_user_guide.pdf", 0 );
	}


	@Override
	public void onAnnotationTapped(AnnotationEvent e, MotionEvent me) {

	}

	@Override
	public boolean onSelectedAnnotationChanging(AnnotationEvent e) {
		return false;
	}

	@Override
	public void onSelectedAnnotationChanged(AnnotationEvent e) {

	}

	private boolean saveBitmap(Bitmap bitmap, String path ){
		return true;
	}
	@Override
	public void onAnnotationAdded(AnnotationEvent e) {
		LogUtil.d( "added nm=" + e.current.getNm() );
		ca = e.current;
	}

	@Override
	public void onAnnotationUpdated(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationImported(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationRemoved(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationFlattened(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationAppearenceChanged(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationLookuped(AnnotationEvent e) {

	}

	@Override
	public void onAnnotationsAllInvalidated() {

	}

	@Override
	public void onPredrawnAnnotationBitmapInvalidated(AnnotationEvent e) {

	}

	@Override
	public void onModifiedCallack(ModifiedCallback callback) {
		LogUtil.d( "pts" + callback.target.getPgPts() );
		LogUtil.d( "old Bounds" + callback.oldBounds );
		lastCallback = callback;
	}
}//method
