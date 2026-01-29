	package udk.android.editor.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.xml.sax.InputSource;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.env.LibLog;
import udk.android.reader.pdf.TextSearchEvent;
import udk.android.reader.pdf.TextSearchListener;
import udk.android.reader.pdf.TextSearchService;
import udk.android.reader.pdf.WatermarkData;
import udk.android.reader.pdf.annotation.FreeTextAnnotation;
import udk.android.reader.pdf.annotation.ImageAnnotation;
import udk.android.reader.pdf.annotation.Annotation;
import udk.android.reader.pdf.annotation.AnnotationEvent;
import udk.android.reader.pdf.annotation.AnnotationListener;
import udk.android.reader.pdf.annotation.AnnotationService;
import udk.android.reader.pdf.annotation.InkAnnotation;
import udk.android.reader.pdf.annotation.LineAnnotation;
import udk.android.reader.pdf.annotation.ModifiedCallback;
import udk.android.reader.pdf.annotation.SquareAnnotation;
import udk.android.reader.pdf.annotation.TextAnnotation;
import udk.android.reader.pdf.form.FormEvent;
import udk.android.reader.pdf.form.FormField;
import udk.android.reader.pdf.form.FormListener;
import udk.android.reader.pdf.quiz.QuizGroup;
import udk.android.reader.pdf.quiz.QuizGroupScore;
import udk.android.reader.pdf.quiz.QuizItem;
import udk.android.reader.pdf.quiz.QuizResultItem;
import udk.android.reader.pdf.quiz.QuizService;
import udk.android.reader.pdf.selection.QuadrangleSelection;
import udk.android.reader.pdf.userdata.InkAnnotationUserData;
import udk.android.reader.pdf.userdata.TextAnnotationUserData.ExNoteIconFactory;
import udk.android.reader.view.pdf.AnimationEvent;
import udk.android.reader.view.pdf.AnimationListener;
import udk.android.reader.view.pdf.InteractionScrollListener;
import udk.android.reader.view.pdf.PDFReadingService;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFViewContextMenuListener;
import udk.android.reader.view.pdf.PDFViewEvent;
import udk.android.reader.view.pdf.PDFViewListener;
import udk.android.reader.view.pdf.PDFViewListenerEx;
import udk.android.reader.view.pdf.scrap.DrawingScrap;
import udk.android.reader.view.pdf.scrap.Scrap;
import udk.android.util.AssignChecker;
import udk.android.util.LogUtil;
import udk.android.util.PlainProcessCallback;
import udk.android.util.ThreadUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.util.vo.menu.ToolMenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestQuiz extends TestBase {
	
	private PDFView pdfView;

	ModifiedCallback lastCallback;

	Annotation ca;
	Annotation ra;

	double[] lastBound;
	int lastPage;

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



	public void unDoMoveAnnot() {
		Annotation annotation = lastCallback.target;
		if (annotation != null) {
			InkAnnotation sa = (InkAnnotation) annotation;
			sa.setPgPts(lastBound);
//			sa.setPage(lastPage);
			pdfView.getAnnotationService().updateAnnotationPage(sa, lastPage);
			pdfView.getAnnotationService().updateFinalizeAnnotationTransformAdj(sa);
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
				LibConfiguration.DEBUGDRAW = true;

				LogUtil.DEBUG = true; // 라이브러리 디버깅용
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.ANNOTATION_APPEARENCE_UPDATE_REQ_POOLING_TERM = 100;
				LibConfiguration.BETA_FREEHAND_DRAWING_CALLBACK_IN_DOUBLEPAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_COVER_EXISTS = false;

//				GlobalConfigurationService.getInstance().setContinuousScrollMode(2);

				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = false;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = true;
				//LibConfiguration.CONTINUOUS_SCROLL_SENSITIVITY = 0.5f;
//				LibConfiguration.PAGE_FOLDING_ACTIVATE_TOLERANCE = 0.5f;

				LibConfiguration.PALM_REJECTION = false;
				LibConfiguration.PAGEMOVE_WITH_PALM_REJECTION = true;
				LibConfiguration.USE_ANNOTATION = true;
				LibConfiguration.USE_ANNOTATION_HANDLE = true;
				LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
				LibConfiguration.USE_ANNOTATION_CREATE_STRIKEOUT = true;
				LibConfiguration.USE_ANNOTATION_CREATE_LINE = true;
				LibConfiguration.USE_ANNOTATION_CREATE_UNDERLINE = true;
				LibConfiguration.USE_ANNOTATION_CREATE_HIGHLIGHT = true;
				LibConfiguration.USE_ANNOTATION_CREATE_POLYGON = true;
				LibConfiguration.USE_ANNOTATION_CREATE_NOTE = true;
				LibConfiguration.USE_ANNOTATION_CREATE_OVAL = true;
				LibConfiguration.USE_ANNOTATION_CREATE_RECTANGLE = true;
				LibConfiguration.USE_SCRAP = true;
				LibConfiguration.USE_ANNOTATION_CREATE_IMAGE = true;
				LibConfiguration.USE_ANNOTATION_CREATE_IMAGE_DRAW = true;
				LibConfiguration.USE_SMART_NAVIGATION = false;
				LibConfiguration.USE_ANNOTATION_CONTEXTMENU = true;
				LibConfiguration.USE_ANNOTATION_CONTEXTMENU_DELETE = false;
				LibConfiguration.SAVE_ANNOTATION_ON_EXPORT = false;
				//LibConfiguration.ANNOTATION_SELECTION_FREEHAND = false //필기 주석 탭 안되도록 적용

				LibConfiguration.CACHING_CURRENT_BASIC_ONLY = false; // 표시하고 있는 페이지 외 다른 페이지도 미리 렌더링 하도록 적용
				LibConfiguration.ENABLE_ANNOTATION_LONGTAP_SELECT = true; // 롱탭으로 주석 선택 가능하도록 적용

				LibConfiguration.USE_STYLUS_ERASER = true; // 펜 버튼 지우개 사용 여부
				LibConfiguration.USE_NOVIEW_LIBRARY = true ;// 노트 생성 기능 사용
				LibConfiguration.USE_PAGE_TRANSFORM = true; // 노트 생성 기능 사용

				LibConfiguration.ANOTATION_IMAGE_DRAW_DELEGATED_DRAW = false; // 페이지 외부에 이미지 표시 기능
				LibConfiguration.USE_ANNOTATION_CREATE_IMAGE_EXTRA_PNG = true; // Undo/Redo시 이미지 처리
				LibConfiguration.XFDF_STAMP_INCLUDE_IMAGE_DATA = true; // 이미지 주석 XFDF로 저장시 이미지 포함
				LibConfiguration.COLOR_32_FOR_TEXTSELECTION = Color.parseColor("#FFCCCCCC"); // 텍스트 선택시 색상
				LibConfiguration.COLOR_32_FOR_USER_DATA = Color.parseColor("#88BBBB00"); // 스크랩 정보 표시 색상
				LibConfiguration.TEXT_SIZE_FOR_USER_DATA = 24f; // 스크랩 정보 표시용 폰트 크기

				LibConfiguration.ANOTATION_IMAGE_STICKER_FOLDER_PATH = "/sdcard";
				LibConfiguration.USE_TOP_LAYER_FREEHAND_ANNOTATION_POINTER = false;
				LibConfiguration.USE_TOP_LAYER_SCREEN_WATERMARKS = true;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_VIEWING = false;
				//LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				//LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;

				//LibConfiguration.USE_ANNOTATION_REMOVE_FLAG = true;
				LibConfiguration.ANNOTATION_CREATE_FREEHAND_METHOD_UPDATE_NEW_TOLERANCE = 300;

				LibConfiguration.USE_IMPORT_XFDF_WITH_SAX = true;

				LibConfiguration.USE_SMART_NAVIGATION = true;

				LibConfiguration.USE_ANNOTATION_SELECTION_CURSOR_QUAD = true;
				LibConfiguration.ANNOTATION_SELECTION_HIGHLIGHT_DASH = true;
				LibConfiguration.SIZE_DIP_CURSOR = 16;
				LibConfiguration.COLOR_32_FOR_SELECTED_BOUNDS = 0xFF6394FD;
				LibConfiguration.ANNOTATION_SELECTION_HIGHLIGHT_DASH_STROKE_EFFECT = new float[]{8f, 6f};
				LibConfiguration.ANNOTATION_SELECTION_HIGHLIGHT_DASH_STROKE_WIDTH = 4;
				LibConfiguration.USE_ANNOTATION_SELECTION_CURSOR_RECT = true;
				LibConfiguration.USE_ANNOTATION_SELECTION_CURSOR_INNER_EMPTY = true;
				LibConfiguration.ANNOTATION_SELECTION_CURSOR_INNER_EMPTY_SROKE_WIDTH = 4;


				ArrayList<Class< ? extends Annotation >> exclude = new ArrayList<>();
				exclude.add(LineAnnotation.class);
				exclude.add(SquareAnnotation.class);
				LibConfiguration.ANNOTATION_SELECTION_HIGHLIGHT_EXCLUDE = exclude;

				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = false;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = true;
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
	private boolean off = false;
	private Annotation lastAnnot = null;


	private int count = 0;
	private byte[] byteXfdf = null;
	private byte[] byteXfdf2 = null;
	private int page = 1;

	private Date date = null;
	@Override
	public Workable< View > getOnTest(){
		final Context context = this;
		return new Workable< View >(){
			@Override
			public void work( View tool ){

				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "테스트", new Runnable(){
					@Override
					public void run(){
						//pdfView.addAnnotationImageStart("/sdcard/rend2.jpg");
/*
						RectF bounds = new RectF(200,200,500,500);
						HashMap<String, Object> userData = new HashMap<>();
						userData.put(AnnotationUserData.KEY_USER_DATA_TYPE, AnnotationUserData.KEY_USER_DATA_TYPE_STICKER);
						userData.put(AnnotationUserData.KEY_USER_DATA_STICKER_PAGE, 1);
						pdfView.addAnnotationImageSync("/sdcard/sticker1_20.pdf", bounds, userData, true);
*/
						/*
						RectF rBound = pdfView.getRenderedPDF().getBounds();
						Log.e("XXX", "added : " + pdfView.getZoom() +", " + pdfView.getRenderedPDF().getBasicZoom());
						float left = rBound.left;
						float top = 200f;
						float right = rBound.left + 1000 * pdfView.getZoom();// rBound.width() * 1.3f * 1.3f;//pdfView.getZoom();
						float bottom = top + 200 * pdfView.getZoom();
						RectF bounds = new RectF(left,top,right,bottom);
						HashMap<String, Object> userData = new HashMap<>();
						userData.put(AnnotationUserData.KEY_USER_DATA_TYPE, AnnotationUserData.KEY_USER_DATA_TYPE_OGTAG);
						userData.put(AnnotationUserData.KEY_USER_DATA_OG_URL, "https://www.naver.com");
						pdfView.addAnnotationImageSync(null, bounds, userData);
*/
/*
						WatermarkData data = new WatermarkData(WatermarkData.SAMPLE_SETTING1);
						data.colorText= Color.BLACK;;//Color.parseColor("#E8E9E9");
						data.fontSize = 28;
						data.vPos = 2;
						data.hPos = 0;
						data.repeat = 1;
						data.rotate=45;
						data.strFile="/sdcard/camera.png";
						pdfView.addWatermark(data);

 */
						/*
						String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Watermark Name=\"Yes24_Watermark_Text\" Guid=\"B25AFB1AD80E4878B88E833FAA0A5D45\" Type=\"0\" Font=\"Segoe UI\" PSFont=\"Segoe UI\" ColorText=\"211,211,211\" FontSize=\"18\" TextAlign=\"1\" FileType=\"0\" Page=\"1\" Zoom=\"100\" Rotate=\"45\" Alpha=\"50\" VPos=\"2\" HPos=\"0\" PosX=\"0\" PosY=\"0\" ScopeType=\"0\" ScopeSubType=\"0\" ScopeStartPage=\"1\" ScopeEndPage=\"1\" ViewType=\"3\" Repeat=\"1\" RepeatVerticalDistance=\"0\" RepeatHorizontalDistance=\"150\"><Text>YES24_TEXT_1234567890_0987654321</Text><File></File></Watermark>";
						WatermarkData data = new WatermarkData(xml);
						pdfView.addWatermark(data);
						 */
						/*

						float x = 1000;
						float y = 500;
						float zoom = pdfView.getZoom();
						PointF pagePoint = pdfView.convertViewPositionToPagePosition(new PointF(x,y));
						RectF pageBounds = new RectF();
						pageBounds.left = pagePoint.x - 250 * zoom;
						pageBounds.right = pagePoint.x + 250 * zoom;
						pageBounds.top = pagePoint.y - 100 * zoom;
						pageBounds.bottom = pagePoint.y + 100 * zoom;
						RectF bounds = pdfView.convertPageBoundsToViewBounds(pageBounds);
						HashMap userData = new HashMap<String, Object >();
						userData.put(AnnotationUserData.KEY_USER_DATA_TYPE, AnnotationUserData.KEY_USER_DATA_TYPE_OGTAG);
						userData.put(AnnotationUserData.KEY_USER_DATA_OG_URL,"http");
						pdfView.addAnnotationImageSync(null, null, bounds, userData);
						 */


						/*
						LibConfiguration.ANNOTATION_AUTO_TEXT_TEXTBOX = true;
						float x = 1000;
						float y = 1000;
						float width = 500;
						float height = 500;
						float DEFAULT_SCRAP_POW_TEXT = 2;
						String content = "123";
						RectF bounds = new RectF(x - width * DEFAULT_SCRAP_POW_TEXT /2,
								y - height * DEFAULT_SCRAP_POW_TEXT/2,
								x + width * DEFAULT_SCRAP_POW_TEXT/2,
								y + height * DEFAULT_SCRAP_POW_TEXT/2);
						LibConfiguration.ANNOTATION_FREETEXT_BOUNDS_DECREASABLE_FOR_TEXT_FIT = false;
						LibConfiguration.ANNOTATION_FONTSIZE_TEXTBOX = 30f;//context.dpToPx(12f)
						LibConfiguration.ANNOTATION_BORDERWIDTH_TEXTBOX = 0f;
						LibConfiguration.ANNOTATION_COLOR_TEXTBOX = Color.BLACK;
						LibConfiguration.ANNOTATION_COLOR_TEXT_TEXTBOX = Color.BLACK;`1
						LibConfiguration.ANNOTATION_COLOR_TEXTBOX_INNER_EXISTS = true;
						LibConfiguration.ANNOTATION_COLOR_TEXTBOX_INNER = Color.parseColor("#F4F2E5");
						pdfView.addAnnotationTextBoxStart( bounds, content, null, false, null );
						 */
//						pdfView.addScrapStart(DrawingScrap.DrawingType.Free, null);
						/*
						TextAnnotationUserData userData = new TextAnnotationUserData();
						userData.exIcon = "0";
						userData.exIconWidth = 50;
						userData.exIconHeight = 50;
						pdfView.addAnnotationNoteStart(userData, null);
						 */

						/*
						List<InstantWatermark> instantWatermarks = new ArrayList<>();
						Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/memo_icon.png");
						InstantWatermark iw = new InstantWatermark(bitmap, 100, Gravity.CENTER, 0);
						instantWatermarks.add(iw);
						iw = new InstantWatermark("Yes24Yes24Yes24Yes24", 100, 100, Color.RED, Gravity.BOTTOM|Gravity.RIGHT, 0);
						instantWatermarks.add(iw);
						pdfView.setScreenWatermarks(instantWatermarks);
						 */

						//pdfView.getTextSearchService().startSearch("형식적 법", false, false, 0);
						//pdfView.addAnnotationImageStart("/sdcard/8월 워치 배경화면 2.png");
						/*
						List<TextParagraph> list = pdfView.getTextParagraphList(pdfView.getPage());
						if(list != null){
							for(TextParagraph tp : list ){
								Log.e("XX", "[" + tp.getText() +"]");

							}
						}
						 */
						/*
						try {
							PDFLibrary.instantNewPDF(context, 800, 600, 1, "/sdcard/memo22.pdf", false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						 */

						/*
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.update;
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_METHOD_UPDATE_NEW_TOLERANCE = 300;
						pdfView.addAnnotationFreehandStart();
						 */


						/*
						ThreadUtil.checkAndRunOnBackgroundThread(new Runnable() {
							@Override
							public void run() {
								long time = System.currentTimeMillis();
								Log.e("XXX","Start QuizTest");
								QuizTest();
								Log.e("XXX","End QuizTest :" + ( System.currentTimeMillis() -time ) );
							}
						});
						 */

						//unDoMoveAnnot();
						/*
						try {


							ArrayList<InstantWatermark> screenWatermarks =  new ArrayList< InstantWatermark >();
							//screenWatermarks.add(new InstantWatermark("KYOBOKYOBOKYOBOKYOBO", 50, 50, Color.RED, Gravity.CENTER, 80));
//							screenWatermarks.add(new InstantWatermark("2025-11-30", 30, 50, Color.RED, Gravity.BOTTOM, 50));
							InputStream logo = context.getAssets().open("kyobo_logo.png");
							Bitmap bitmap = BitmapFactory.decodeStream(logo);
							screenWatermarks.add(new InstantWatermark(
									Arrays.asList(
											new InstantWatermark.InstantWatermarkText(bitmap, 400, 112, 100, 10 ),
											new InstantWatermark.InstantWatermarkText("KYOBOKYOBOKYOBOKYOBO", 50, 50, Color.RED, 0, null ),
											new InstantWatermark.InstantWatermarkText("2025-11-30", 30, 50, Color.BLUE, 10, null)
									),
									Gravity.NO_GRAVITY, Paint.Align.CENTER,true,500, 80, 45f));
							//screenWatermarks.add(new InstantWatermark(Arrays.asList("KYOBOKYOBOKYOBOKYOBO","2025-11-30"), Arrays.asList(50, 50), 50, Color.RED, Gravity.BOTTOM, 20));
							pdfView.setScreenWatermarks(screenWatermarks);
							pdfView.requestRendering();

							InputStream bitmap = context.getAssets().open("watermark_image.dat");
							byte[] tBytesBitmap = null;
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
								tBytesBitmap = bitmap.readAllBytes();
							}
							WatermarkData tDataBitmap = new WatermarkData();
							tDataBitmap.formXML(tBytesBitmap);
							tDataBitmap.strFile = "/sdcard/kyobo_logo_2x.png";
							pdfView.addWatermark(tDataBitmap);

							InputStream text = context.getAssets().open("watermark_text.dat");
                            byte[] tBytes = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                tBytes = text.readAllBytes();
                            }
                            WatermarkData tData = new WatermarkData();
							tData.formXML(tBytes);
							tData.strText= "anonymous12345";
							pdfView.addWatermark(tData);

						} catch (Exception e) {
							 e.printStackTrace();
						}
				*/
						/*
						LibConfiguration.USE_SCRAP = true;
						LibConfiguration.SCRAP_DRAW_INNER_AREA_EXIST = false;
						LibConfiguration.COLOR32_AREASELECTION_DRAWING_STROKE = 0xFF314FB9;//Color.rgb(49,79,185);

						LibConfiguration.SCRAP_DRAWING_INNER_AREA_EXIST = true;
						LibConfiguration.COLOR32_AREASELECTION_DRAWING_FILL = 0x50EEF2FC;//Color.argb( 200,238,242,252);
						LibConfiguration.SCRAP_DRAWING_OTHER_STROKE_PAINT = true;
						LibConfiguration.SCRAP_DRAWING_OTHER_STROKE_WIDTH = 3;
						LibConfiguration.SCRAP_DRAWING_OTHER_PATH_EFFECT = null;//new float[]{ 10, 5 };
						LibConfiguration.COLOR32_AREASELECTION_DRAWING_OTHER_STROKE =  0xFF314FB9;//Color.rgb(49,79,185);

						pdfView.addScrapStart(DrawingScrap.DrawingType.Rectangle, new Workable<Scrap>() {
							@Override
							public void work(Scrap scrap) {
								//pdfView.uiAddScrapEndConfirm("/sdcard/aaa.png", "/sdcard/aaa2.png", null);
							}
						});
						 */
						/*
						LibConfiguration.ANNOTATION_CONTENTS_OPENTYPE = 4;
						LibConfiguration.ANNOTATION_FREETEXT_BOUNDS_VERTICAL_EXPANDABLE = false;
						LibConfiguration.USE_ANNOTATION_FREETEXT_AUTOFIT_WHEN_INVISIBLE = false;
						pdfView.addAnnotationTextBoxStart(new Workable<Annotation>() {
							@Override
							public void work(Annotation annotation) {
								pdfView.getAnnotationService().select(annotation);
								pdfView.editSelectedTextBoxContents();
								//								pdfView.getExecForAnnotationEditTextBox( ( FreeTextAnnotation ) annotation ).run();
							}
						});

						 */

					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - create", new Runnable(){
					@Override
					public void run(){
						count = 10;

						switch (count){
							case 0:
								//LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.end;
								pdfView.addAnnotationFreehandStart();
								break;
							case 1:
								pdfView.addAnnotationNoteStart();
								break;
							case 2:
								pdfView.addAnnotationRectangleStart();
								break;
							case 3:
								pdfView.addAnnotationOvalStart();
								break;
							case 4:
								pdfView.addAnnotationLineStart();
								break;
							case 5:
								pdfView.addAnnotationArrowStart();
								break;
							case 6:
								/*
								LibConfiguration.ANNOTATION_COLOR_POLYGON_INNER_EXISTS = true;
								LibConfiguration.ANNOTATION_BORDERSTYLE = Annotation.BORDER_DASHED;
								LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{3,1};
								 */
								pdfView.addAnnotationPolygonStart(5);
								break;
							case 7:
								pdfView.addAnnotationImageStart("/sdcard/camera.png");
								break;
							case 8:
								LibConfiguration.ANNOTATION_COLOR_TEXTBOX = 0xFFFF0000;
								LibConfiguration.ANNOTATION_COLOR_TEXT_TEXTBOX = 0xFF0000FF;
								LibConfiguration.ANNOTATION_COLOR_TEXTBOX_INNER_EXISTS = true;
								LibConfiguration.ANNOTATION_COLOR_TEXTBOX_INNER = 0xFF00FF00;
								pdfView.addAnnotationTextBoxStart();
								break;
							case 9:
								pdfView.addAnnotationTypeWriterStart();
								break;
							case 10:
								pdfView.addAnnotationHighlightStart();
								break;
							case 11:
								pdfView.addAnnotationStrikeOutStart();
								break;
							case 12:
								pdfView.addAnnotationUnderlineStart();
								break;
						}

//						LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_NONE;
//						pdfView.zoomToFit();

						//pdfView.zoomToFit();
						//pdfView.requestRendering();
						//RectF rectF = pdfView.getPageBounds();
						//pdfView.addAnnotationFreehandStart(true, rectF); // pageBounds에만 필기 주석 시작
						//pdfView.addAnnotationTextBoxStart();
						//pdfView.addAnnotationTypeWriterStart();
//						LibConfiguration.PALM_REJECTION = false;
//						LibConfiguration.PAGEMOVE_WITH_PALM_REJECTION = true;

						//pdfView.addAnnotationImageStart("/sdcard/camera.png");
						//pdfView.addAnnotationFileAttachmentStart("/sdcard/camera.png", null);
//						LibConfiguration.ANNOTATION_BORDERWIDTH_FREEHAND = 20;
						//LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.update;
						//LibConfiguration.ANNOTATION_CREATE_FREEHAND_OPTIMIZE_DISTANCE = 0.3f;
						//LibConfiguration.ANNOTATION_CREATE_FREEHAND_OPTIMIZE_ANGLE = 3f;
						//pdfView.addAnnotationFreehandStart();
						//RectF bounds = new RectF(200,200,300,300);
						//HashMap<String, Object> userData = new HashMap<>();
						//userData.put(SquareAnnotationUserData.KEY_USER_DATA_TYPE, SquareAnnotationUserData.KEY_USER_DATA_TYPE_OGTAG);
						//userData.put(SquareAnnotationUserData.KEY_USER_DATA_OG_URL, "https://www.naver.com");
						//pdfView.addAnnotationRectangleStart(bounds, null, userData, null);

/*
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.end;
						LibConfiguration.ANNOTATION_BORDERWIDTH_FREEHAND = 40;
						InkAnnotationUserData userData = new InkAnnotationUserData();
						userData.type = InkAnnotationUserData.STYLE.basic;
						pdfView.addAnnotationFreehandStart(userData);


 */
/*
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.end;
						InkAnnotationUserData userData = new InkAnnotationUserData();
						userData.type = InkAnnotationUserData.STYLE.tape;
						LibConfiguration.ANNOTATION_COLOR_FREEHAND = Color.parseColor("#FFF17A95");
						LibConfiguration.ANNOTATION_COLOR_FREEHAND_INNER = Color.parseColor("#FFF17A95");
						LibConfiguration.ANNOTATION_BORDERWIDTH_FREEHAND = 20f;
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_TAPE_STROKE_WIDTH = 2f;
						//LibConfiguration.ANNOTATION_CREATE_FREEHAND_TAPE_STROKE_INNER_ALPHA_WHEN_HIDDEN = 200;
//						LibConfiguration.ANNOTATION_CREATE_FREEHAND_TAPE_STROKE_OUTTER_ALPHA_WHEN_HIDDEN = 100;
						LibConfiguration.ANNOTATION_BORDERSTYLE = Annotation.BORDER_DASHED;
						LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{3,1};
						pdfView.addAnnotationFreehandStart(userData);

 */
						//pdfView.addAnnotationRectangleStart();
						//pdfView.addAnnotationLineStart();
						//pdfView.addAnnotationOvalStart();`
						//pdfView.addAnnotationPolygonStart(5);
						//LibConfiguration.USE_TTS = true;
						/*
						pdfView.startReading();
						 */
						//pdfView.addAnnotationNote( null, "xxxxx", 400, 500, null, null, null );
						//pdfView.addAnnotationNoteStart();
						//pdfView.addAnnotationHighlightStart();
						//pdfView.addAnnotationHighlightStartContinuous();
//						pdfView.addAnnotationTypeWriterStart();
						//pdfView.addAnnotationTextBoxStart();
						//pdfView.addAnnotationImageStart(null);//"/sdcard/test.jpg");
						//RectF bounds = new RectF(100, 100, 300, 300);
						//pdfView.addAnnotationTextBoxStart(bounds, "1234567890", null, false, null);


/*
						//LibConfiguration.ANNOTATION_FREEHAND_POINTER_BLUR_WIDTH = 20f;
						//LibConfiguration.ANNOTATION_FREEHAND_POINTER_BLUR_STYLE = BlurMaskFilter.Blur.NORMAL;
						LibConfiguration.USE_ANNOTATION_FREEHAND_AUTO_REMVOE_SUSPEND = true;
						//LibConfiguration.ANNOTATION_FREEHAND_AUTO_REMVOE_FRAME = 24;
						//LibConfiguration.ANNOTATION_FREEHAND_AUTO_REMVOE_DURATION_MILLIS = 1000;
						LibConfiguration.ANNOTATION_BORDERWIDTH_FREEHAND = 20f;
						LibConfiguration.ANNOTATION_COLOR_FREEHAND_INNER = Color.WHITE;
						LibConfiguration.ANNOTATION_CREATE_FREEHAND_LINE_METHOD = InkAnnotationUserData.METHOD.remove;
						InkAnnotationUserData userData = new InkAnnotationUserData();
						userData.type = InkAnnotationUserData.STYLE.pointer;
						pdfView.addAnnotationFreehandStart(userData);

 */

/*
						if( lastAnnot != null) {
							pdfView.getAnnotationService().registAnnotationToCacheList(lastAnnot);
							lastAnnot.getUserDataList().remove(WebViewerClientService.KEY_UPDATE_SERVER);
							pdfView.getAnnotationService().addImageEndConfirm((ImageAnnotation) lastAnnot, true);
						}
 */

						//pdfView.addAnnotationRectangleStart();
//						pdfView.addAnnotationFreehandStart();
//						pdfView.addAnnotationFileAttachmentStart("/sdcard/test.jpg", null);
						//pdfView.addAnnotationImageStart("/sdcard/test.jpg");
//						pdfView.scrap("/sdcard/aaa.png");
						//pdfView.uiAddScrapEndConfirm("/sdcard/aaa.pdf", "/sdcard/aaa.png", null);

						//Bitmap bitmap = pdfView.getPDF().getRenderedSinglePageBitmap(1, 2.0f);

//						off = !off;
//						pdfView.hideScreenWatermarks(off);
						/*
						Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/d0fea308-3bb2-4629-8a2b-7940dd590e77.png");
						float posX = 100 - (bitmap.getWidth() / 2);
						float posY = 100 - (bitmap.getHeight()) / 2;
						float width = bitmap.getWidth();
						float height = bitmap.getHeight();
						Rect bitmapBounds = new Rect(0, 0, (int)bitmap.getWidth(), (int)bitmap.getHeight());
						RectF annotBounds = new RectF(posX, posY, posX + width, posY + height);
						pdfView.addAnnotationImageSync(bitmap, bitmapBounds, annotBounds, null);

						 */
						/*
						try {
							Bitmap thumbnail = pdfView.thumbnail(1, 500);
							BitmapConvertFile(thumbnail, "/sdcard/1111.jpg");
							Log.e("XXX", "bitmap");
						} catch (Exception e) {

						}
						 */


						LibConfiguration.USE_ANNOTATION_CREATE_POLYGON_FIXED = true;
						//LibConfiguration.USE_ANNOTATION_CREATE_POLYGON_EXPAND = true;
						LibConfiguration.ANNOTATION_BORDERSTYLE = Annotation.BORDER_SOLID;//Annotation.BORDER_DASHED;
						//LibConfiguration.ANNOTATION_LINEDASHPATTERN = new double[]{1,3};
						LibConfiguration.ANNOTATION_BORDERWIDTH_POLYGON = 40;
						LibConfiguration.ANNOTATION_BORDERWIDTH_FIGURE = 40;
						LibConfiguration.ANNOTATION_COLOR_FIGURE_INNER_EXISTS = false;
						//pdfView.addAnnotationRectangleStart();
						pdfView.addAnnotationPolygonStart(3);
						//pdfView.addAnnotationCloudyOvalStart();
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - end", new Runnable(){
					@Override
					public void run(){
//						pdfView.addScrapEndCancel();
/*
						LibConfiguration.USE_SCRAP = true;
						pdfView.addScrapStart(DrawingScrap.DrawingType.Free, new Workable<Scrap>() {
							@Override
							public void work(Scrap scrap) {
								pdfView.scrap("/sdcard/aaa.png");
							}
						});
 */

						if( pdfView.addAnnotationFreehandCanUndo()){
							pdfView.addAnnotationFreehandEndConfirm();
						} else {
							pdfView.addAnnotationFreehandEndCancel();
						}

						//pdfView.addScrapEndCancel();

						pdfView.getInteractionService().setDragListener( null );

					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트1 - clear start", new Runnable(){
					@Override
					public void run(){
						List< Class< ? extends Annotation > > includeOnly  = new ArrayList<>();
						includeOnly.add( InkAnnotation.class );

						pdfView.clearAnnotationStart(null, includeOnly);
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
						/*
						try {
							pdfView.flatten();
						} catch (PDFException e) {
							e.printStackTrace();
						}
						 */
						if(off){
							pdfView.onscreenAllMarkupAnnotations();
						} else {
							pdfView.offscreenAllMarkupAnnotations();
						}
						off= !off;


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
						//pdfView.save();
						pdfView.uiSaveAs("/sdcard/empty6.pdf", false, false, (PlainProcessCallback) null);
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트3 - export", new Runnable(){
					@Override
					public void run()
					{
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Log.e("XXX", "export start");
									StringWriter value = new StringWriter();
									//LibConfiguration.ANNOTATION_IMPORT_EXPORT_FREEHAND_OPTIMIZE_DISTANCE = 0.3f;
									//LibConfiguration.ANNOTATION_IMPORT_EXPORT_FREEHAND_OPTIMIZE_ANGLE = 14f;
									pdfView.exportXFDF(value);
									String result = value.toString();
									byteXfdf = result.getBytes();
									writeToFile("/sdcard/test.xfdf", byteXfdf);
									Log.e("XXX", "export end : " + byteXfdf.length);
									//byteXfdf = null;

								} catch (Exception e ){
									Log.e("XXX", "export Exception");
									e.printStackTrace();
								}

							}
						}).start();

					}//method
				} ) );

				mcs.add( new MenuCommand( "테스트4 - import", new Runnable(){
					@Override
					public void run(){
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									if( byteXfdf == null )
										byteXfdf = readFromFile("/sdcard/test.xfdf");
									//LibConfiguration.ANNOTATION_IMPORT_EXPORT_FREEHAND_OPTIMIZE_DISTANCE = 0.3f;
									//LibConfiguration.ANNOTATION_IMPORT_EXPORT_FREEHAND_OPTIMIZE_ANGLE = 4.0f;

									Log.e("XXX", "import start");
									if( byteXfdf != null && byteXfdf.length > 0){
										try {
											pdfView.importXFDF(byteXfdf);
										} catch (Exception e){
											e.printStackTrace();
										}

//										byteXfdf = null;
									}
									Log.e("XXX", "import Done");
								} catch ( Exception ex ){
									ex.printStackTrace();
									Log.e("XXX", "import Exception");
								}
							}
						}).start();

					}
				} ) );

				mcs.add( new MenuCommand( "테스트4 - import SAX", new Runnable(){
					@Override
					public void run(){
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Log.e("XXX", "import start");
									try {
										InputStream in = new FileInputStream("/sdcard/test.xfdf");
										InputSource inputSource = new InputSource(new InputStreamReader(in));
					//					pdfView.importXFDF(inputSource);
									} catch (Exception e){
										e.printStackTrace();
									}

									Log.e("XXX", "import Done");
								} catch ( Exception ex ){
									ex.printStackTrace();
									Log.e("XXX", "import Exception");
								}
							}
						}).start();

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

						//pdfView.nextPage();
						pdfView.page(150);
					}
				} ) );
				mcs.add( new MenuCommand( "페이지 리셋", new Runnable(){
					@Override
					public void run(){
						pdfView.resetPage(pdfView.getPage());
					}
				} ) );
				mcs.add( new MenuCommand( "단면/양면 보기", new Runnable(){
					@Override
					public void run(){
//						LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
						pdfView.setDoublePageViewing(!pdfView.isDoublePageViewing());
					}
				} ) );
				WidgetFactory.uiPopupMenu( tool, mcs );

			}//method
		};
	}//method

	private void QuizTest(){

		QuizService quizService = pdfView.getQuizService();
		List<QuizGroup> list = quizService.lookupQuiz();
		Log.e("XXX","Group Count :"+ list.size());
		for(QuizGroup grp : list){
			Log.e("XXX","Group Start :"+grp.getGroupName());
			List<String> titles = grp.getGroupAreaTitle();
			List<Integer> pages = grp.getGroupAreaPage();
			int maxCount = Math.max(titles.size(), pages.size());
			for ( int index = 0 ; index < maxCount ; index ++ ){
				String title = titles.size()<=index?"UNKNOWN":titles.get(index);
				int page = pages.size()<=index?-1:pages.get(index);
				Log.e("XXX","AREA : page(" + page + "), name(" + title +")");
				if( title == null || TextUtils.isEmpty(title) ){
					Log.e("XXX","AREA : ====================== Empty : Name ==========================");
				}
			}

			FormField ff = grp.getGroupArea();

			if(ff == null || maxCount == 0){
				Log.e("XXX","AREA : ====================== Empty ==========================");
			}

			/*
			if( ff != null ) {
				List<Annotation> annots = pdfView.getPDF().getFormService().getFieldAnnotations(ff);
				if( AssignChecker.isAssigned( annots ) ){
					for( Annotation annot : annots ){
						int page = annot.getPage();
						String name = annot.getNm();
						Log.e("XXX","AREA : page(" + page + "), name(" + name +")");
						if( name == null || TextUtils.isEmpty(name) ){
							Log.e("XXX","AREA : ====================== Empty : Name ==========================");
						}
					}
				}
			} else {
				Log.e("XXX","AREA : ====================== Empty ==========================");
			}
			 */

			QuizGroupScore qgs = grp.getGroupScore();
			if( qgs != null ) {
				FormField score = qgs.getScore();
				if (score != null) {
					List<Annotation> annots = pdfView.getPDF().getFormService().getFieldAnnotations(score);
					if (AssignChecker.isAssigned(annots)) {
						for (Annotation annot : annots) {
							int page = annot.getPage();
							String name = annot.getNm();
							String content = annot.getContents();
							Log.e("XXX", "Score : page(" + page + "), Perfect Score(" + qgs.getPerfectScore() + ")");
						}
					}
				}
			}

			Log.e("XXX","Item Count : " + grp.getItemCount());
			for ( int index = 0 ; index < grp.getItemCount() ; index++ ){
				QuizItem item = grp.getItem(index);
				FormField question = item.getQuestion();
				if( question == null ){
					Log.e("XXX","Question : ====================== Empty ==========================");
				}

				String correctAnswer = pdfView.getPDF().getQuizService().getQuizCorrectAnswerInfo( item );
				Log.e("XXX", "Question : page(" + item.getQuestionPage() + "), name(" + item.getQuestionTitle() +"), Score(" + item.getItemScore() +"), CorrectAnswer(" + correctAnswer+")");
				if( item.getQuestionTitle() == null || TextUtils.isEmpty(item.getQuestionTitle())){
					Log.e("XXX","Question : ====================== Name Empty ==========================");
				}

				/*
				List<Annotation> annots = pdfView.getPDF().getFormService().getFieldAnnotations(question);
				if (AssignChecker.isAssigned(annots)) {
					for (Annotation annot : annots) {
						int page = annot.getPage();
						String name = annot.getNm();
						String content = annot.getContents();
						Log.e("XXX", "Question : page(" + page + "), name(" + name +"), Score(" + item.getItemScore() +"), CorrectAnswer(" + correctAnswer+")");
						if( name == null || TextUtils.isEmpty(name)){
							Log.e("XXX","Question : ====================== Name Empty ==========================");
						}
					}
				}
				 */

				FormField answer = item.getSingleAnswer();
				if( answer == null ){
					Log.e("XXX","Answer : ====================== Empty ==========================");
				}
				List<Annotation> annots = pdfView.getPDF().getFormService().getFieldAnnotations(answer);
				if (AssignChecker.isAssigned(annots)) {
					for (Annotation annot : annots) {
						int page = annot.getPage();
						String name = annot.getNm();
						String content = annot.getContents();
						Log.e("XXX", "Answer : page(" + page + "), count("+ annots.size() + "), Type(" + item.getItemType()+")");
						break;
					}
				}
			}

			if( grp.getGroupCheck() == null ) {
				Log.e("XXX","Check : ====================== Empty : Group Check  ==========================");
			}

			if( grp.getGroupClear() == null ) {
				Log.e("XXX","Clear : ====================== Empty : Group Clear ==========================");
			}

			Log.e("XXX","Group End :"+grp.getGroupName());
		}
		List< QuizResultItem > results = quizService.getQuizResults();
		//Log.e("XXX"," ======== quiz result : " + results.size());
	}

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
				e.printStackTrace();
			}
			return null;
	}

	private void writeToFile(String path, byte[] data ){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch ( Exception ex ){
				ex.printStackTrace();
			}
		}
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

		setCallback();

		if(Build.VERSION.SDK_INT >= 30){
			if( !Environment.isExternalStorageManager() ) {
				try {
					Uri uri = Uri.parse("package:udk.android.pdfeditorlib.dev");
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

	ProgressDialog pd = null;

	private void setCallback(){

		pdfView.getAnimationService().addListener(new AnimationListener() {
			@Override
			public void onAnimationEnded(AnimationEvent e) {
				pdfView.requestRendering();
			}
		});


		pdfView.getAnnotationService().addImportAnnotationByPageListener(new AnnotationService.ImportAnnotationByPageListener() {
			@Override
			public void onStart(List<Integer> page) {
				Log.e("XXX", "onStart");
				/*
				pdfView.post(new Runnable() {
					@Override
					public void run() {
						if( pd == null ){
							pd = ProgressDialog.show( TestQuiz.this, null, Message.PROCESSING, true, false, null );
						}

						pd.show();
					}
				});
				 */
			}

			@Override
			public void onEnd(List<Integer> page) {
				Log.e("XXX", "onEnd");
				/*
				pdfView.post(new Runnable() {
					@Override
					public void run() {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
					}
				});
				 */
			}
		});

		pdfView.getTextSearchService().addListener(new TextSearchListener() {
			@Override
			public void onTextSearchStarted(TextSearchEvent e) {
				Log.e("XXXX", "onTextSearchStarted");
			}

			@Override
			public void onTextSearchedInPage(TextSearchEvent e) {
				Log.e("XXXX", "onTextSearchedInPage");
			}

			@Override
			public void onTextSearchFinished(TextSearchEvent e) {
				TextSearchService tss = pdfView.getTextSearchService();
				int pages = tss.getSearchedPageCount();

				if( pages > 0 ) {
					for (int index = 0; index < pages ; index ++ ) {
						int page = tss.getSearchedPageForPosition(index);
						List<QuadrangleSelection> listData = tss.getSearchedForPosition(index);
						for (QuadrangleSelection data : listData) {
							Log.e("XXXX", "onTextSearchFinished : [" + page + "] - [" + data.getContent() +"]");
						}
					}
				}
			}

			@Override
			public void onTextSearchDisposed(TextSearchEvent e) {

			}

			@Override
			public void onTextSearchProcess(int page, int total) {
				Log.e("XXXX", "onTextSearchProcess " + page + "/" + total);
			}
		});

		pdfView.getInteractionService().setScrollListener(new InteractionScrollListener() {
			@Override
			public boolean onScrollStart(int page) {
				Log.e("XXX","onScrollStart");
				return false;
			}

			@Override
			public boolean onScrollEnd(int page, boolean isMax) {
				Log.e("XXX","onScrollEnd : " + isMax);
				return false;
			}
		});


		pdfView.setPDFViewListener(new PDFViewListener() {
			@Override
			public void onOpenCompleted(PDFViewEvent e) {
				//Log.e("UNIDOCSNDK","screencapture : " + pdfView.okToScreencapture() +", " + pdfView.getPDF().getPDFVersionSL());
				//Log.e("XXXX","onOpen :" + pdfView.getPageWidth(1, 1.0f) +", " + pdfView.getPageHeight(1, 1.0f));
				/*
				new Thread(new Runnable() {
					@Override
					public void run() {
						QuizTest();
					}
				}).start();
				 */
			}

			@Override
			public void onSavedAs(PDFViewEvent e) {

			}

			@Override
			public void onPageChanged(PDFViewEvent e) {
				/*
				List<TextParagraph> list = pdfView.getTextParagraphList(pdfView.getPage());
				if(list != null){
					for(TextParagraph tp : list ){
						Log.e("XX", "[" + tp.getText() +"]");

					}
				}
				 */
			}

			@Override
			public void onZoomChanged(PDFViewEvent e) {
				Log.e("XXX", "onZoomChanged : ["+ pdfView.isInBasicZoom() +", "+ e.zoom);

			}

			@Override
			public void onSingleTapUp(PDFViewEvent e) {
				Log.e("XXXX", "onSingleTapUp");

				/*
				float x = e.motionEvent.getX();
				float y  = e.motionEvent.getY();
				RectF bounds = new RectF(x, y, x+300, y+300);
				HashMap<String, Object> userData = new HashMap<String, Object>();
				userData.put(AnnotationUserData.KEY_USER_DATA_S_NM, "블록암호블록암호블록암호블록암호블록암호블록암호블록암호");
				userData.put(AnnotationUserData.KEY_USER_DATA_S_FN, "블록암호 LEA 소스코드 사용 매뉴얼(v1.0).pdf");
				userData.put(AnnotationUserData.KEY_USER_DATA_S_PN, 1);
				userData.put(AnnotationUserData.KEY_USER_DATA_S_R, bounds.left + "," + bounds.top);
				pdfView.addAnnotationTextBoxStart(bounds, "1234567890", userData, false, null);
*/

			}

			@Override
			public void onDoubleTapUp(PDFViewEvent e) {
				Log.e("XXXX", "onDoubleTapUp");
			}

			@Override
			public void onLongPress(PDFViewEvent e) {
				Log.e("XXXX", "onLongPress");
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
				/*
				if( pdfView.addAnnotationFreehandCanUndo()){
					pdfView.addAnnotationFreehandEndConfirm();
				} else {
					pdfView.addAnnotationFreehandEndCancel();
				}
				 */
			}

			@Override
			public void onUndo(String xfdf) {

			}

			@Override
			public void onRedo(String xfdf) {

			}

			@Override
			public void onEndConfirm(String xfdf) {
				//pdfView.addAnnotationFreehandStart();
			}

			@Override
			public void onEndCancel(int page, String uid) {

			}
		});

		pdfView.setPDFViewListenerEx(new PDFViewListenerEx() {
			@Override
			public void onPageChangeTried(PDFViewEvent e) {
				/*
				if(pdfView.addAnnotationFreehandCanUndo()) {
					pdfView.addAnnotationFreehandEndConfirm(new Runnable() {
						@Override
						public void run() {
							pdfView.addAnnotationFreehandStart();
						}
					});
				} else
					pdfView.addAnnotationFreehandEndCancel();

				 */
				Log.e("XXX","onPageChangeTried : " + e.page);
			}
		});

		pdfView.setOnViewPageChangeListener(new PDFView.OnViewPageChangeListener() {
			@Override
			public void onViewPageChange(int page) {
				Log.e("XXX","onViewPageChange :" + page);
			}
		});

		pdfView.setPDFReadingCallback(new PDFReadingService.PDFReadingCallback() {
			@Override
			public void onStart() {

			}

			@Override
			public void onPause() {

			}

			@Override
			public void onResume() {

			}

			@Override
			public void onEnd() {
				Log.e("XXX", "onEnd");
			}
		});


		pdfView.setPDFViewContextMenuListener(new PDFViewContextMenuListener() {
			@Override
			public boolean onActivateContextMenuForSelectedText(RectF boundsInView) {
				Log.e("XXXX", "onActivateContextMenuForSelectedText: " + pdfView.getSelectedText());
				return false;
			}

			@Override
			public boolean onActivateContextMenuForSelectedAnnotation(RectF boundsInView) {
				Log.e("XXXX", "onActivateContextMenuForSelectedAnnotation");
				return true;
			}

			@Override
			public void onDeactivateContextMenu() {
				Log.e("XXXX", "onDeactivateContextMenu");
			}
		});

		pdfView.getAnnotationService().addListener(new AnnotationListener() {

			@Override
			public void onAnnotationTapped(AnnotationEvent e, MotionEvent me) {
				Log.e("XXXX", "===== onAnnotationTapped : " + e.current.getRefNo());
				if(e.current instanceof InkAnnotation){
					InkAnnotation ia = (InkAnnotation) e.current;
					Log.e("XXXX","isOptimized : " +ia.isOptimized());
				}

				Log.e("XXXX","onAnnotationTapped : " +e.current.getContents());
			}

			@Override
			public void onAnnotationLongTapped( final AnnotationEvent e, final MotionEvent me ){
				Log.e("XXXX", "======== onAnnotationLongTapped: " + e.current.getRefNo());
								pdfView.editSelectedTextBoxContents();
			}

			@Override
			public boolean onSelectedAnnotationChanging(AnnotationEvent e) {
				Log.e("XXXX", "======== onSelectedAnnotationChanging: " + ( e.current!= null?e.current.getRefNo():"Null"));
				return false;
			}

			@Override
			public void onSelectedAnnotationChanged(AnnotationEvent e) {
				Log.e("XXXX", "======== onSelectedAnnotationChanged: " + ( e.current!= null?e.current.getRefNo():"Null"));
			}

			@Override
			public void onAnnotationAdded(AnnotationEvent e) {
				LogUtil.d( "added nm=" + e.current.getNm() );
				ca = e.current;
				lastAnnot = e.current;
				Annotation annot = e.current;
				String xfdf = e.current.exportToXFDF();
				if(annot instanceof ImageAnnotation) {
					if(((ImageAnnotation)annot).isOGTag() ) {
						View view = new View(getBaseContext()); // OGTag뷰로 변경
						view.setBackgroundColor(Color.YELLOW);
						pdfView.addInstantViewOverAnnot(view, annot);
					}
				}
			}

			@Override
			public void onAnnotationUpdated(AnnotationEvent e) {

			}

			@Override
			public void onAnnotationImported(AnnotationEvent e) {

				if( e.currents != null){
					Log.e("XXX", "imported : " + e.currents.size() );
					Log.e("XXX", "imported : " +  pdfView.getAnnotationIds(pdfView.getPage()).size());
				}
			}

			@Override
			public void onAnnotationRemoved(AnnotationEvent e) {
				//lastAnnot = e.current;
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
				lastBound = callback.oldBounds;
				lastPage = callback.oldPage;
			}
		});


		pdfView.setOnClearAnnotationDragListener(new PDFView.OnClearAnnotationDragListener() {
			@Override
			public void onClearDragStart(boolean byStylus, MotionEvent e) {
				if( byStylus ){
					if( pdfView.isNowCreatingAnnotationFreehand() ){
						if(pdfView.addAnnotationFreehandCanUndo()){
							pdfView.addAnnotationFreehandEndConfirm(new Runnable() {
								@Override
								public void run() {
									pdfView.addAnnotationFreehandStart();
								}
							});
						}
					}
				}
			}

			@Override
			public void onClearDragMove(boolean byStylus, MotionEvent e) {

			}

			@Override
			public void onClearDragEnd(boolean byStylus, MotionEvent e) {

			}
		});

		pdfView.setExNoteIconFactory( new ExNoteIconFactory(){

			@Override
			public Bitmap getNoteIcon(String exIcon, boolean isSelected) {
				Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/memo_icon.png");
				return bitmap;
			}
		});
		LibConfiguration.USE_TTS = true;
		List< MenuCommand > commands = new ArrayList< MenuCommand >();
		commands.add( new MenuCommand( "Read", new Runnable(){
			@Override
			public void run(){
				pdfView.startInstantReading( pdfView.getSelectedText() );
			}//method
		} ) );

		pdfView.addMenuCommandsForSelectedText( commands, true );

		pdfView.getPDF().getFormService().addListener(new FormListener() {
			@Override
			public void onFieldValueUpdated(FormEvent e) {
				Log.e("XXXX", "onFieldValueUpdated : " + e.current.getTitle());
			}

			@Override
			public void onActionFormSubmit(FormEvent e) {
				Log.e("XXXX", "onActionFormSubmit : " + e.uri);
			}

			@Override
			public void onActionFormReset(FormEvent e) {
				Log.e("XXXX", "onActionFormReset : " + e.uri);
			}
		});

		pdfView.getPDF().getQuizService().setInstanceQuizGradeListener(new QuizService.InstanceQuizGradeListener() {


			@Override
			public void onMarkGradeStart(String groupName) {
				Log.e("XXXX", "onMarkGradeStart : " + groupName);
			}

			@Override
			public void onMarkGradeEnd(String groupName) {
				Log.e("XXXX", "onMarkGradeEnd : " + groupName);
			}

			@Override
			public void onUnmarkGradeStart(String groupName) {
				Log.e("XXXX", "onUnmarkGradeStart : " + groupName);
			}

			@Override
			public void onUnmarkGradeEnd(String groupName) {
				Log.e("XXXX", "onUnmarkGradeEnd : " + groupName);
			}
		});
	}

	private void openPDF() {

		try {
/*
			ExtraOpenOptions eoos = new ExtraOpenOptions();
			eoos.encryptedDrmParamExtraExtern = "token=" + Base64.encodeToString(strLoginToken.getBytes(), Base64.NO_WRAP);
			eoos.encryptedDrmFileSavePath = getCacheDir().getAbsolutePath();
*/

			/*
			String filePath = "/sdcard/yoonseo1407/ebook/pdf/EB100000165.pdf";
			String key = "903A184BA1A24C918888897371B69519";
			ExtraOpenOptions eoos = new ExtraOpenOptions();
			eoos.autoDRMOpen = false;
			// 주석 입력이 가능하려면 활성화 필요
			eoos.forceCopy = true;
			eoos.forceAddNotes = true;
			eoos.encryptedDrmFileSavePath = filePath;
			pdfView.openPDF(filePath, key, key, 1, 1f, true, eoos, null);
			 */
			//pdfView.openPDF( "/sdcard/test/ezPDF Webviewer_매뉴얼.pdf", "abcd123!", "abcd123!", 0, 1, true, null );
			///pdfView.openPDF("/sdcard/test/ezPDF Webviewer_매뉴얼.pdf", "abcd123!", "abcd123!", 1, 1, true, null, null);
//			pdfView.openPDF("/sdcard/test/D210927080.pdf", 1 );
//			pdfView.openPDF("/sdcard/test/2.pdf", 1 );
//			pdfView.openPDF("/sdcard/test/480D210728370 구문도해 영어구문론.pdf", 1 );
			//pdfView.openPDF("/sdcard/test/9791188116133_key_89422928820D4AC4AA5A722904BBB116.pdf", "89422928820D4AC4AA5A722904BBB116", "89422928820D4AC4AA5A722904BBB116", 1, 1f, true, null, null);
			//pdfView.openPDF("/sdcard/test/865c2a3037734c648f0b791434d2441f.pdf", 556 );
			//pdfView.openPDF("/sdcard/test/테스트_주석_사용.pdf", 1 );

			//pdfView.openPDF("/sdcard/test/D210927080.pdf", 4 );

//			pdfView.openPDF("/sdcard/test/XAV-742_712BT.pdf", 1 );
			//pdfView.openPDF("/sdcard/note_temp_01.pdf", 1 );
//			pdfView.openPDF("/sdcard/emptynote50.pdf", 1);
//			pdfView.openPDF("/sdcard/android.pdf", 1);
//			pdfView.openPDF("/sdcard/yes24/unidocs/note/pdf/240823152318.pdf", 1 );
//			pdfView.openPDF("/sdcard/2025 PSK 경찰학 합격노트+a.pdf", 4 );
//			pdfView.openPDF("/sdcard/하이라이트 테스트.pdf", 1 );
			//pdfView.openPDF("/sdcard/fe887dfd44e6491e9d6f28a69924651b.pdf", 9 );
			//pdfView.openPDF("/sdcard/애린 왕자.pdf", 1 );
			//pdfView.openPDF("/sdcard/김영삼 회고록.pdf", 1 );
			//pdfView.openPDF("/sdcard/test/4801162673325_2023 행정사 계약법 사례단문집.pdf", 1 );
			//pdfView.openPDF("/sdcard/enc_e9788954767323en240522_2.pdf", 1 );
			//pdfView.openPDF("/sdcard/[천재교육]문제풀이 유형_S_v1_SAMPLE_20180919.pdf", 1 );
			//pdfView.openPDF("/sdcard/_ip_quiz_named.pdf", 1 );
			//pdfView.openPDF("/sdcard/empty.pdf", 1 );//메모.pdf", 1 );
			//pdfView.openPDF("/sdcard/empty6.pdf", 1 );//메모.pdf", 1 );
			//pdfView.openPDF("/sdcard/메모(첨부파일 없는 파일).pdf", 1 );//메모.pdf", 1 );
			//pdfView.openPDF("/sdcard/yesIssue_250311.pdf", 1 );
			//pdfView.openPDF("/sdcard/1997649_2025 자이스토리 공통수학1.pdf", 1 );
			//pdfView.openPDF("/sdcard/2024년_5월_고3_영어_문제지(객관식_정오답체크).pdf", 1 );
			//pdfView.openPDF("/sdcard/2025년_수능연계완성(객관식_주관식_1~33).pdf", 1 );
			//pdfView.openPDF("/sdcard/01593_20250429113048.pdf", 1 );
			//pdfView.openPDF("/sdcard/[샘플]2026학년도 FINAL 실전모의고사 영어영역_본문_20250701.pdf", 1 );
			//pdfView.openPDF("/sdcard/[샘플2]2026학년도_FINAL_실전모의고사_국어영역_본문_20250702(선택포함).pdf", 1 );
			//pdfView.openPDF("/sdcard/서울대 한국어 1A Workbook(멀티eBook).pdf", 157 );
			//pdfView.openPDF("/sdcard/kissue250704.pdf", 1 );
			//pdfView.openPDF("/sdcard/2026학년도_FINAL_실전모의고사_사회탐구영역_한국지리_test.pdf", 1 );
			//pdfView.openPDF("/sdcard/[완]2026학년도 FINAL 실전모의고사 수학영역.pdf", 1 );
			//pdfView.openPDF("/sdcard/[완료_수정]2026학년도 FINAL 실전모의고사 수학영역.pdf", 1 );
			//pdfView.openPDF("/sdcard/2026학년도 만점마무리 봉투모의고사 고난도 Hyper_본문_수정_완.pdf", 1 );
			//pdfView.openPDF("/sdcard/[완료_수정]2026학년도 FINAL 실전모의고사 수학영역_250804.pdf", 1 );
			//pdfView.openPDF("/sdcard/EB100001983.pdf", 1);
			//pdfView.openPDF("/sdcard/AAA.pdf", 1);

			//pdfView.openPDF("/sdcard/WM-TXT-EAD9120B362E4A4F85997873DEA3BFA2.PDF", 1 );
			//pdfView.openPDF("/sdcard/이펙티브 타입스크립트.pdf", 1 );
			//pdfView.openPDF("/sdcard/저가 매수의 기술.pdf", 2);
			//pdfView.openPDF("/sdcard/2027학년도 수능특강(생명과학1)본문_test.pdf", 1 );
			pdfView.openPDF("/sdcard/pdf_magenta_white_dot.pdf", 1 );


			/*
			ExtraOpenOptions eoos = new ExtraOpenOptions();
			eoos.autoDRMOpen = false;
			// 주석 입력이 가능하려면 활성화 필요
			eoos.forceCopy = true;
			eoos.forceAddNotes = true;
			eoos.encryptedDrmFileSavePath = "/sdcard/EB100001983.pdf";
			String key = "824AD27B21A74B92AE06EFF4F58B089A";
			pdfView.openPDF("/sdcard/EB100001983.pdf", key, key, 83, 1f, true, eoos, null);
			 */
		} catch ( Exception e ){

		}
//		pdfView.openPDF( "http://stage.ibk.co.kr/fup/customer/form/2021121015525472454931559495957.pdf", 1 );
		//pdfView.openPDF( "/sdcard/test/1334067_카티아 도움닫기.pdf", 1 );
//		pdfView.openPDF( "https://manuals.info.apple.com/MANUALS/1000/MA1595/en_US/ipad_user_guide.pdf?filename=ipad_user_guide.pdf", 0 );
	}
}//method
