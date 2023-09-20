package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestMultimedia extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	
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
				
				
//				LibConfiguration.USE_QUIZ = true;
//				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				
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
				mcs.add( new MenuCommand( "연속 자동 재생 off", new Runnable(){
					@Override
					public void run(){
						
						pdfView.setContinuousAudioPlayMode(false);
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "연속 자동재생 true", new Runnable(){
					@Override
					public void run(){
						
						pdfView.setContinuousAudioPlayMode(true);
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "테스트", new Runnable(){
					@Override
					public void run(){
						
						pdfView.playFirstAudioInDocuemnt();
						
					}//method
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
		pdfView.openPDF( "/sdcard/친구가필요해.pdf", 1, null );
		
//		pdfView.setMediaPlayStateListener( new PDFView.MediaPlayStateListener(){
//			
//			@Override
//			public void onPlayStateChanged(){
//				Alerter.shortNotice( context, "media play state change" );
//			}
//			
//			@Override
//			public void onDeactivate(){
//				Alerter.shortNotice( context, "media deactivate" );
//			}
//			
//			@Override
//			public void onActivate(){
//				Alerter.shortNotice( context, "media activate" );
//			}
//		} );
		
//		pdfView.openPDF( "/sdcard/test/movietest3.pdf", 1, null );
		
	}//method

}//method
