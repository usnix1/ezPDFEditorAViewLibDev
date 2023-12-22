package udk.android.editor.test;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.GlobalConfigurationService;
import udk.android.reader.view.pdf.PDFView;
import udk.android.pdfreaderlib.R;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestBuiltInAnnotation extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
		
				LibConfiguration.DEBUGDRAW = false;
				
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				
				LibConfiguration.USE_ANNOTATION_CONTEXTMENU = true;
				LibConfiguration.USE_ANNOTATION_OPEN = true;
				LibConfiguration.USE_ANNOTATION_ADD_CONTEXTMENU = false;
				LibConfiguration.USE_ANNOTATION_CONTENTS_PREVIEW = false;
				LibConfiguration.USE_ANNOTATION_CONTEXTMENU_CANCEL = false;
				LibConfiguration.USE_ANNOTATION_CONTEXTMENU_FLATTEN = false;
				LibConfiguration.USE_ANNOTATION_CONTENTS_PREVIEW = false;
				LibConfiguration.USE_ANNOTATION_REPLY = false;
				LibConfiguration.USE_TEXTSELECTION_MAGNIFIER = false;
				LibConfiguration.ENABLE_DIRECT_USER_INPUT = false;
				
				
				GlobalConfigurationService.getInstance().disableTextSelection();
				
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
				mcs.add( new MenuCommand( "Note", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateNote();
					}
				} ) );
				mcs.add( new MenuCommand( "Freehand", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateFreehand();
					}
				} ) );
				mcs.add( new MenuCommand( "TextBox", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateTextBox();
					}
				} ) );
				mcs.add( new MenuCommand( "Highlight", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateHighlight();
					}
				} ) );
				mcs.add( new MenuCommand( "StrikeOut", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateStrikeOut();
					}
				} ) );
				mcs.add( new MenuCommand( "Rectangle", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateRectangle();
					}
				} ) );
				mcs.add( new MenuCommand( "Oval", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateOval();
					}
				} ) );
				mcs.add( new MenuCommand( "Line", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateLine();
					}
				} ) );
				mcs.add( new MenuCommand( "Arrow", new Runnable(){
					@Override
					public void run(){
						getPDFView().uiAnnotV2CreateArrow();
					}
				} ) );
				
				WidgetFactory.uiPopupMenu( tool, mcs );
				
			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		pdfView = getPDFView();
		pdfView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( R.anim.loading ) );
		pdfView.fastOpenWebPDF( "https://www.kocca.kr/knowledge/abroad/deep/__icsFiles/afieldfile/2012/06/22/iNHMXTXguV6o.pdf", 1 );
		
	}//method

}//method
