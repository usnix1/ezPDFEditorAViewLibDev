package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestVisang extends TestBase{
	
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
			
				LogUtil.DEBUG = true;
				LibConfiguration.DEBUGDRAW = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
//				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
//				LibConfiguration.DOUBLE_PAGE_COVER_EXISTS = true;
				
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_NONE;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				
				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				
//				LibConfiguration.EBOOK_MODE_PAGE_FOLDING_WITH_USER_INTERACTION = false;
//				LibConfiguration.EBOOK_MODE_PAGING_WITH_FLING = false;
				LibConfiguration.USE_EBOOK_MODE = false;
				
				/* ------------------------------------------------------------------- */
				
				LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
				LibConfiguration.USE_TOP_LAYER_ANNOTATION = true;
				LibConfiguration.USE_ANNOTATION_CREATE_POLYGON = true;
				LibConfiguration.XFDF_STAMP_INCLUDE_IMAGE_DATA = true;
				
				LibConfiguration.TEXT_SEARCH_INCLUDE_TEXT = true;
				LibConfiguration.TEXT_SEARCH_INCLUDE_TEXT_WORD_INDEX_COUNT = 3;
				
//				LibConfiguration.USE_MEDIA_CONTROL_TOOLBAR = true;
				LibConfiguration.LINK_HIGHLIGHT = false;
				
//				LibConfiguration.USE_ANNOTATION_ROTATION_POLYGON = true;
				LibConfiguration.SUPPORT_BGM = true;
				LibConfiguration.USE_BGM = true;
				LibConfiguration.USE_IN_PDF_SCROLLBAR = true;
				LibConfiguration.USE_MULTIPLE_MEDIA_HANDLE = true;
				
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR = true;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_SOUND = false;
				LibConfiguration.USE_MEDIA_ADVANCED_CONTROL_TOOLBAR_FOR_VIDEO = true;
				
				LibConfiguration.USE_COMBO_WIDGET_DISPLAY_ARROW = false;
				
//				LibConfiguration.USE_MEDIA_PLAYER_DEFAULT_EX = true;
				
			}//method
		};
	}//method

	
	@Override
	public Workable< View > getOnTest(){
		final Context context = this;
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				
				mcs.add( new MenuCommand( "Next Page", new Runnable(){
					@Override
					public void run(){
						
						pdfView.nextPage();
					}//method
				} ) );
				
				mcs.add( new MenuCommand( "Previous Page", new Runnable(){
					@Override
					public void run(){
						
						pdfView.prevPage();
					}//method
				} ) );
				
				mcs.add( new MenuCommand( "Init Page", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPage(pdfView.getPage());
					}//method
				} ) );
				
				mcs.add( new MenuCommand( "delete all", new Runnable(){
					@Override
					public void run(){
						
						pdfView.deleteAllAnnotation(pdfView.getPage());
					}//method
				} ) );
				
				
				WidgetFactory.uiPopupMenu( tool, mcs );
				
			}//method
		};
	}//method
//	LinearLayout toolbarlayout;

	boolean isShow = true;
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		pdfView = getPDFView();
//		pdfView.setInstanceFactoryForPDFMediaView(new InstanceFactory<PDFMediaPlayView>() {
//			
//			@Override
//			public PDFMediaPlayView newInstance() {
//				return new ExMediaPlayView(context);
//			}                                                                                                                                             
//		});
//		
//		pdfView.setInstanceFactoryForMediaControlToolbar(new InstanceFactory<PDFMediaControlToolbar>() {
//			
//			@Override
//			public PDFMediaControlToolbar newInstance() {
//				return new ExMediaControlToolbar(context);
//			}
//		});
		
//		pdfView.setInstanceFactoryForPDFMediaPlayerView(new InstanceFactory<PDFMediaPlayerView>() {
//			
//			@Override
//			public PDFMediaPlayerView newInstance() {
//				// TODO Auto-generated method stub
//				return new PDFMediaPlayerViewExtern(TestVisang.this);
//			}
//		}, true);
		
//		pdfView.setOnMediaPopupListener(new OnMediaPopupListener() {
//			
//			@Override
//			public void onPopup(View v) {
//				Log.e("", "onPopup 2");
//			}
//			
//			@Override
//			public void onPopdown(View v) {
//				Log.e("", "onPopdown 2");
//			}
//			
//			@Override
//			public void onPDFPopUp(Uri uri) {
//				Log.e("", "onPDFPopUp 2");
//			}
//			
//			@Override
//			public void onPDFHiddenWindow(Uri uri) {
//				Log.e("", "onPDFHiddenWindow 2");
//			}
//			
//			@Override
//			public void onPDFFullScreen(Uri uri) {
//				Log.e("", "onPDFFullScreen 2");
//			}
//			
//			@Override
//			public void onFullScreenStart(View v) {
//				Log.e("", "onFullScreenStart 2");
//			}
//			
//			@Override
//			public void onFullScreenEnd(View v) {
//				Log.e("", "onFullScreenEnd 2");
//			}
//		});
		
		
		pdfView.openPDF( "/sdcard/test/ready_mat_06_pdf3_v04.pdf", 1 );
		
	}//method

}//method
