package udk.android.editor.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Properties;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.env.LibConstant;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.ui.PDFUIView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;


/**
 *
 * @author JEON YONGTAE
 */
public abstract class TestUIBase extends Activity{
	
	public RelativeLayout getRootContainer(){
		return rootContainer;
	}//method
	
	public PDFUIView getPDFUIView(){
		return pdfUiView;
	}//method
	
	public int getPDFViewWidth(){
		return RelativeLayout.LayoutParams.MATCH_PARENT;
	}//method
	
	public int getPDFViewHeight(){
		return RelativeLayout.LayoutParams.MATCH_PARENT;
	}//method
	
	public abstract Runnable getAdditionalConfiguration();
	public abstract Workable< View > getOnTest();
	
	private RelativeLayout rootContainer;
	private PDFUIView pdfUiView;
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		LogUtil.DEBUG = true;
		LibConfiguration.DEBUGDRAW = true;
		LibConfiguration.OPEN_METHOD_NO_CACHING = false;
		LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_DEBUG;
		
		LibConfiguration.USE_ANNOTATION = true;
		LibConfiguration.USE_ANNOTATION_HANDLE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
		LibConfiguration.USE_ANNOTATION_CREATE_HIGHLIGHT = true;
		LibConfiguration.USE_ANNOTATION_CREATE_LINE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_NOTE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_OVAL = true;
		LibConfiguration.USE_ANNOTATION_CREATE_RECTANGLE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_STRIKEOUT = true;
		LibConfiguration.USE_ANNOTATION_CREATE_TEXTBOX = true;
		LibConfiguration.USE_ANNOTATION_CREATE_UNDERLINE = true;
		LibConfiguration.USE_TOOLBAR = true;
		LibConfiguration.USE_ANNOTATION_CONTEXTMENU = true;
		LibConfiguration.USE_ANNOTATION_ADD_CONTEXTMENU = true;
		
		LibConfiguration.USE_TEXTSELECTION_MAGNIFIER = true;
		
		LibConfiguration.OTHERPAGE_TILEWORK = true;
		LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
		LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
		LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_WIDTHFIT;
		LibConfiguration.USE_EBOOK_MODE = true;
		
		LibConfiguration.ENABLE_DIRECT_USER_INPUT = true;
		
		LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
		
		Runnable additionalConfiguration = getAdditionalConfiguration();
		if( additionalConfiguration != null ){
			additionalConfiguration.run();
		}//if
		
		final LinearLayout ll = new LinearLayout( this );
		ll.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams lps = null;
		
		Properties initParams = new Properties();
		pdfUiView = new PDFUIView( this, initParams, null );
		
//		try{
//			Class cls = Class.forName( "udk.ezpdfview.sample.fastopen.R.anim" );
//			int loading = ( Integer ) cls.getDeclaredField( "loading" ).getInt( null );
//			pdfUiView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( loading ) );
//		}catch( Exception ex ){
//			LogUtil.e( ex );
//		}//try
		
//		pdfUiView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( udk.ezpdfview.sample.fastopen.R.anim.loading ) );

		final Workable< View > onTest = getOnTest();
		if( onTest != null ){
			
			final Button btn = new Button( this );
			btn.setText( "test" );
			btn.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick( View v ){
					
					onTest.work( btn );
					
				}//method
			} );
			lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
			lps.weight = 0;
			ll.addView( btn, lps );
			
		}//if
		
		RelativeLayout rl = new RelativeLayout( context );
		
		lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		lps.weight = 1;
		ll.addView( rl, lps );
		
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( getPDFViewWidth(), getPDFViewHeight() );
		rl.addView( pdfUiView, rlps );
		
		rootContainer = new RelativeLayout( context );
		rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
		rootContainer.addView( ll, rlps );
		
		setContentView( rootContainer );

	}//method
	
	@Override
	public void onPause(){
		
		Context context = this;
		
		//save page state
		
		try{
			
			if( pdfUiView != null ){
				PDFView pdfView = pdfUiView.getPDFView();
				if( pdfView != null ){
					pdfView.savePageState();
				}//if
			}//if
			
		}catch( Exception ex ){
			LogUtil.e( ex );
		}//try
		
		super.onPause();
	}//method
	
	@Override
	public void onResume(){

		super.onResume();
		
		final Context context = this;
		
		//XXX rootView 가 널이면 안됨. 그런 상황도 있을 수가 있는 걸까.. 일단은 회피코드를 넣어놓음
		final PDFView pdfView = pdfUiView != null ? pdfUiView.getPDFView() : null;
		
		//menu & toolbar
		
//		try{
//			
//			if( pdfUiView != null ){
//				PDFUIViewManageService vms = pdfUiView.getViewManageService();
//				if( vms != null ){
//					vms.uiUpdateLayoutTitleBarState();
//				}//if
//			}//if
//
//		}catch( Exception ex ){
//			LogUtil.e( ex );
//		}//try
		
		try{
			
			if( LibConfiguration.USE_TOOLBAR && pdfView.isOpened() && pdfUiView != null ){
				pdfUiView.uiToolbar();
			}//if
			
		}catch( Exception ex ){
			LogUtil.e( ex );
		}//try
		
		//book direction
		
		try{
		
			if( pdfView != null && pdfView.isOpened() ){
				if( pdfView.getBookDirectionSetting() == LibConstant.BOOK_READ_DIRECTION_FOLLOW_DEFAULT_SETTING ){
					int old = pdfView.getBookDirection();
					pdfView.setBookDirection( LibConfiguration.DEFAULT_BOOK_READ_DIRECTION, false );
					if( pdfView.getBookDirection() != old ){
						int current = pdfView.getPage();
						if( current > 1 ){									
							pdfView.updateFitDirectly( current - 1 );
							pdfView.nextPage();
						}else if( current < pdfView.getPageCount() ){
							pdfView.updateFitDirectly( current + 1 );
							pdfView.prevPage();
						}//if						
					}//if
				}//if
			}//if
			
		}catch( Exception ex ){
			LogUtil.e( ex );
		}//try
		
		// overlay menu button
		
		if( pdfUiView != null ){
			pdfUiView.setOverlayMenuButtonEnabled( true );
		}//if
		
	}//method	

	@Override
	public void onBackPressed(){
		
		if( pdfUiView != null ){
			pdfUiView.uiClose( new Workable< Boolean >(){
				@Override
				public void work( Boolean ok ){
					finish();
				}//method
			} );
		}//if
		
	}//method



}//class