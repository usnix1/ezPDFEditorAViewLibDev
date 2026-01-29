package udk.android.editor.test;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.pdf.ExtraOpenOptions;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.IOUtil;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.Alerter;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestEzPDFDRM extends TestBase{
	
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
			
				IOUtil.hostnameVerifier = new HostnameVerifier() {
					
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				};
				LogUtil.DEBUG = true;
				LibConfiguration.DEBUGDRAW = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
				LibConfiguration.USE_QUIZ = true;
//				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
//				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
//				LibConfiguration.DOUBLE_PAGE_COVER_EXISTS = true;
				
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_NONE;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = true;
				
				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				
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
//				LibConfiguration.USE_SINGLE_EACH_MEDIA_HANDLE = true;
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
//				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_DRAG_DROP = false;
				
				LibConfiguration.USE_IN_PDF_LEFT_TOP_ALIGN = true;
				LibConfiguration.USE_SYNC_CONCURRENT_ANIMATION_OBJECT = true;
				
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_VIDEO = true;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_SOUND = true;
				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR_WITH_TEXT_HIGHLIGHT = true;
				
				LibConfiguration.USE_EZPDFDRM_SKIP_REFERENCE = true;
			}//method
		};
	}//method

	
	private boolean toPrev;
	
	@Override
	public Workable< View > getOnTest(){
		final Context context = this;
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "채점하기", new Runnable(){
					@Override
					public void run(){
						
//						pdfView.getQuizService().markGrade();
						Alerter.shortNotice( context, "파일 이름 : " + pdfView.getPDFTitle() );
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "채점지우기", new Runnable(){
					@Override
					public void run(){
						
						pdfView.getQuizService().unmarkGrade();
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지에 문제풀이 있는지 확인", new Runnable(){
					@Override
					public void run(){
						
						boolean hasQuiz = pdfView.hasQuiz( pdfView.getPage() );
						Alerter.shortNotice( context, "페이지 " + pdfView.getPage() + " : " + hasQuiz );
						                                                                                                                                                                                                                                                 
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지에 클리커 있는지 확인", new Runnable(){
					@Override
					public void run(){
						
						boolean hasClicker = pdfView.hasQuizClicker( pdfView.getPage() );
						Alerter.shortNotice( context, "페이지 " + pdfView.getPage() + " : " + hasClicker );
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지 클리커 리셋", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPageQuizClicker( pdfView.getPage() );
						 
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지 날리지탭 리셋", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPageQuizKnowledgeTap( pdfView.getPage() );
						
					}//method
				} ) );			
				mcs.add( new MenuCommand( "이 페이지 리셋", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPage( pdfView.getPage() );
						
					}//method
				} ) );		
				mcs.add( new MenuCommand( "단면/양면 전환", new Runnable(){
					@Override
					public void run(){
						
						pdfView.setDoublePageViewing( !pdfView.isDoublePageViewing() );
						
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
						
//						pdfView.nextPage();
						pdfView.getQuizResults();
						
					}
				} ) );
				mcs.add( new MenuCommand( "암호화", new Runnable(){
					@Override
					public void run(){
						
						makeDRMFile();
						
					}
				} ) );
				WidgetFactory.uiPopupMenu( tool, mcs );
				
			}//method
		};
	}//method                   
	
	private static final String FILE_PDF = "/sdcard/sample.pdf";
	private static final String FILE_PDF_EN = "/sdcard/sample_en.pdf";
	private static final String FILE_PDF_COVER = "/sdcard/DRMCover.pdf";
	private static final String DRM_COVER_PW = "{B6406229-4DCC-4F36-ABCE-71D2CC3F633D}";
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		
		if( true ){
			pdfView.openPDF( FILE_PDF, 1 );
		} else {
			ExtraOpenOptions eoos = new ExtraOpenOptions();
			eoos.encryptedDrmParamExtraExtern = "token="+ Base64.encodeToString("95e20684-e95d-4a42-b226-cd4281aa9c49".getBytes(), Base64.DEFAULT);
			pdfView.openPDF( FILE_PDF_EN, 1, eoos );
		}
		
		

	}//method
	
	private void makeDRMFile(){

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				
				String enckey = UUID.randomUUID().toString().replace("-", "");
				String docID = UUID.randomUUID().toString().replace("-", "");
				
				boolean enablePrint = false;
				boolean enableCopyText = false;
				boolean rtn = false;
				
				rtn = pdfView.encryptByDeviceKeysEx( FILE_PDF, FILE_PDF_EN, docID, enckey, getDRMInfoStr(enablePrint, enableCopyText, null), null, null, FILE_PDF_COVER, DRM_COVER_PW);
				Log.e("", "RTN1 " + rtn);
				
				if( rtn ){
					String filename = FILE_PDF_EN.substring(FILE_PDF_EN.lastIndexOf("/"));
					String rtnMsg = pdfView.sendEncryptByDeviceKeysEx(EZPDFEDITOR_DRM_SERVER_NAME, EZPDFEDITOR_DRM_GETPK_NAME, EZPDFEDITOR_DRM_SET_POLICY_NAME, EZPDFEDITOR_DRM_HTTS, EZPDFEDITOR_DRM_SERVER_PORT, true, 
							setDRMPolicy( docID, enckey, filename, enablePrint, enableCopyText, 100, 100, 100, 100, 100, false, null, "", null),
							"token="+ Base64.encodeToString("95e20684-e95d-4a42-b226-cd4281aa9c49".getBytes(), Base64.DEFAULT) );
					
					Log.e("", "MSG : " + rtnMsg);
					
				}
				
				return null;
			}
			
		}.execute();
	}
	
	private static final int EZPDFEDITOR_DRM_SERVER_PROTOCOL = 1;
	private static final int EZPDFEDITOR_DRM_SERVER_PORT = 80;
	private static final String EZPDFEDITOR_DRM_SERVER_NAME = "52.79.237.2";
	private static final String EZPDFEDITOR_DRM_GETPK_NAME = "/drm/ezpdfgetpk";
	private static final String EZPDFEDITOR_DRM_SET_POLICY_NAME = "/drm/ezpdfsetpolicy";//"/drm/setdrm";
	private static final boolean EZPDFEDITOR_DRM_HTTS = false;
	
//	private static final int EZPDFEDITOR_DRM_SERVER_PROTOCOL = 2;
//	private static final int EZPDFEDITOR_DRM_SERVER_PORT = 443;
//	private static final String EZPDFEDITOR_DRM_SERVER_NAME = "ezpdf.unidocs.co.kr";
//	private static final String EZPDFEDITOR_DRM_GETPK_NAME = "/drm/ezpdfgetpk";
//	private static final String EZPDFEDITOR_DRM_SET_POLICY_NAME = "/drm/ezpdfsetpolicy";//"/drm/setdrm";
//	private static final boolean EZPDFEDITOR_DRM_HTTS = true;
	
	String getDRMInfoStr(boolean enablePrint, boolean enableCopyText, String szComment)
	{
		String strInfo = "#INFO STRUCTURE BEGIN\n";
		strInfo += "comment=";
		if (TextUtils.isEmpty(szComment)) {
			strInfo += szComment;
			strInfo += "\n";
		} else {
			strInfo += "ezPDF Editor DRM\n";
		}
		
		strInfo += "method=ezpdfeditor\n";
		strInfo += "handshake=35\n";
		strInfo += String.format(Locale.ENGLISH, "server=%s,\n", EZPDFEDITOR_DRM_SERVER_NAME);
		strInfo += String.format(Locale.ENGLISH, "port=%d,\n", EZPDFEDITOR_DRM_SERVER_PORT);
		strInfo += String.format(Locale.ENGLISH, "protocol=%s,\n", EZPDFEDITOR_DRM_SERVER_PROTOCOL >= 2 ? "https" : "http");
		strInfo += String.format(Locale.ENGLISH, "getpk=%s,\n", EZPDFEDITOR_DRM_GETPK_NAME);
		strInfo += "open=3,/drm/ezpdfgetkey\n";

		if (enablePrint)
		{
			strInfo += "print=3,/drm/ezpdfgetprint\n";
			strInfo += "printlog=3,/drm/ezpdfgetprintlog\n";
		}
		else{
			strInfo += "print=2,\n";
			strInfo += "printlog=2,\n";
		}

		if (enableCopyText)
		{
			strInfo += "copytext=3,/drm/ezpdfcopytext\n";
		}
		else
		{
			strInfo += "copytext=2,\n";
		}

		return strInfo;
	}
	

	String setDRMPolicy( String szDocID, String szEncKey, String szName, boolean enablePrint, boolean enableCopyText, int nOpenCount, int nPrintCount, int nCopyText, int nTimeBomb, int nUserCnt, boolean bChekIP, String pszIP, String pszOS, String szUserIDs)
	{
		String strArgs = null ;
		try {
			
			strArgs = "docid=" + szDocID;
			strArgs += "&enc_key=" + szEncKey;
			strArgs += "&docname=" +  URLEncoder.encode(szName, "UTF-8");
		
	
			strArgs += "&enable_print=" + (enablePrint == true ? "Y" : "N");
			strArgs += "&enable_copytext=" + (enableCopyText == true ? "Y" : "N");
		
			strArgs += "&count_open=" + nOpenCount;
			strArgs += "&count_print=" + nPrintCount;
			strArgs += "&count_copytext=" + nCopyText;
			strArgs += "&Timebomb=" + nTimeBomb;
	
			strArgs += "&limit_number_of_users=" + nUserCnt;
			strArgs += "&check_ip=" + (bChekIP == true ? "Y" : "N");
			strArgs += "&open_only_specific_ip=";
			strArgs += "&open_only_specific_system=" + pszOS;
	
			if (TextUtils.isEmpty(szUserIDs) == false)
				strArgs += "&open_only_email_user=" + URLEncoder.encode(szUserIDs, "UTF-8");
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return strArgs;
	}

}//method
