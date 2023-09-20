package udk.android.editor.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;


/**
 *
 * @author JEON YONGTAE
 */
public abstract class TestBase extends Activity{
	
	public File getWorkspace(){
		return workspace;
	}
	
	public RelativeLayout getRootContainer(){
		return rootContainer;
	}//method
	
	public ViewGroup getParentContainer(){
		return rl;
	}
	
	public PDFView getPDFView(){
		return pdfView;
	}//method
	
	public int getPDFViewWidth(){
		return RelativeLayout.LayoutParams.MATCH_PARENT;
	}//method
	
	public int getPDFViewHeight(){
		return RelativeLayout.LayoutParams.MATCH_PARENT;
	}//method
	
	public abstract Runnable getAdditionalConfiguration();
	public abstract Workable< View > getOnTest();
	
	private RelativeLayout rl;
	
	private RelativeLayout rootContainer;
	private PDFView pdfView;
	
	private File workspace;
	
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
		LibConfiguration.USE_ANNOTATION_CREATE_FILEATTACHMENT = true;
		LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
		LibConfiguration.USE_ANNOTATION_CREATE_HIGHLIGHT = true;
		LibConfiguration.USE_ANNOTATION_CREATE_IMAGE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_IMAGE_DRAW = true;
		LibConfiguration.USE_ANNOTATION_CREATE_LINE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_NOTE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_OVAL = true;
		LibConfiguration.USE_ANNOTATION_CREATE_RECTANGLE = true;
		LibConfiguration.USE_ANNOTATION_CREATE_STRIKEOUT = true;
		LibConfiguration.USE_ANNOTATION_CREATE_TEXTBOX = true;
		LibConfiguration.USE_ANNOTATION_CREATE_UNDERLINE = true;
		LibConfiguration.USE_ANNOTATION_TYPEWRITER = true;
		LibConfiguration.USE_ANNOTATION_CREATE_TYPEWRITER = true;
		LibConfiguration.USE_TOOLBAR = true;
		LibConfiguration.USE_ANNOTATION_CONTEXTMENU = true;
		LibConfiguration.USE_ANNOTATION_ADD_CONTEXTMENU = true;
		
		LibConfiguration.USE_TEXTSELECTION_MAGNIFIER = true;
		
		LibConfiguration.OTHERPAGE_TILEWORK = true;
		LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
		LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
		LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_WIDTHFIT;
		LibConfiguration.USE_EBOOK_MODE = true;
		
		LibConfiguration.USE_FORM = true;
		LibConfiguration.ENABLE_DIRECT_USER_INPUT = true;
		LibConfiguration.USE_SIGNATURE_TOOLKIT = true;
		LibConfiguration.USE_SIGNATURE_NPKI = true;
		LibConfiguration.USE_SIGNATURE_INNER_HANDLING = true;
		LibConfiguration.USE_SCRAP = true;
		LibConfiguration.USE_PAGE_TRANSFORM = true;
		LibConfiguration.USE_PAGE_TRANSFORM_PUNCH = true;
		
		LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
		
		workspace = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test" );
		if( !workspace.exists() || !workspace.isDirectory() ){
			workspace.mkdirs();
		}
		
		Runnable additionalConfiguration = getAdditionalConfiguration();
		if( additionalConfiguration != null ){
			additionalConfiguration.run();
		}//if
		
		final LinearLayout ll = new LinearLayout( this );
		ll.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams lps = null;
		
		pdfView = new PDFView( context );
		
//		PDFViewBottomToolbar bottomToolbar = new PDFViewBottomToolbar( context );
//		pdfView.setBottomToolbar( new PDFViewBottomToolbar[]{ bottomToolbar }, null, null );
		
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
		
		rl = new RelativeLayout( context );
		
		lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		lps.weight = 1;
		ll.addView( rl, lps );
		
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( getPDFViewWidth(), getPDFViewHeight() );
		rl.addView( pdfView, rlps );
		
//		if( pdfView.hasBottomToolbar() ){
//			lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
//			lps.weight = 0;
//			ll.addView( bottomToolbar, lps );
//		}//if
		
		
		rootContainer = new RelativeLayout( context );
		rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
		rootContainer.addView( ll, rlps );
		
		setContentView( rootContainer );

	}//method

	
	@Override
	public void onBackPressed(){
		Context context = this;
		final ProgressDialog pd = ProgressDialog.show( context, null, "문서를 닫는 중입니다", true, false );
		new Thread(){
			@Override
			public void run(){
				
				pdfView.closePDF();
				
				runOnUiThread( new Runnable(){
					@Override
					public void run(){
						pd.dismiss();
						finish();
					}
				} );
			};
		}.start();
		
	}//method


}//class