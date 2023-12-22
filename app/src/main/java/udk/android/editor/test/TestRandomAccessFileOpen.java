package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.pdf.EDDataProvider;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.Workable;
import udk.android.widget.Alerter;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestRandomAccessFileOpen extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	private PDFView pdfView;

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		pdfView.setOnPDFOpenFailureListener( new PDFView.OnPDFOpenFailureListener(){
			@Override
			public void onPDFOpenFailure( String errorMessage ){
				Alerter.shortNotice( context, "못열어! %s", errorMessage );
			}//method
		} );
		
		pdfView.openPDF(new EDDataProvider() {
			
			RandomAccessFile file ;
			@Override
			public int size() {
				try {
					
//					Log.e("", "size");
					
					if( file != null)
						return (int) file.length();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return 0;
			}
			
			@Override
			public boolean saveAs(String path) {
//				Log.e("", "saveAs : " + path );
				return false;
			}
			
			@Override
			public void open() {
				try {
					
					if(file == null)
						file = new RandomAccessFile( "/sdcard/I Can Share_WB.pdf", "rw" );
					
//					Log.e("", "open file size : " + file.length()) ;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public int getBytes(ByteBuffer buffer, int offset, int size) {
				
				try {
//					Log.e("", "getBytes offset : " + offset + ", size : " + size + " file size " + file.length()) ;
					
					if( file != null){
						
						if( buffer == null )
							return 0;
						
						if(offset > file.length())
							return 0;
						
						byte[] read = new byte[ size ];
						
						buffer.clear();
						file.seek(offset);
						int count = file.read(read);
						
						if(count > 0){
							buffer.put(read);
						}
						
						return count<0?0:count;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return 0;
			}
			
			@Override
			public void close() {
				try {
					if(file != null)
						file.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1);
		
	}//method

}//method
