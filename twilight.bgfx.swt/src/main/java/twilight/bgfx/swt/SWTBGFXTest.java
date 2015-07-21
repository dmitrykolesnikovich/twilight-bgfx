package twilight.bgfx.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import twilight.bgfx.BGFX;

public class SWTBGFXTest {

    public static void main(String[] args) {
        Display display = Display.getDefault();
        
        Shell shell = new Shell(display);
        shell.setSize(1024, 1024);
        shell.setText("Test");
        
        final Composite composite = new Composite(shell, SWT.NONE);
        
        composite.setLocation(25, 25);
        composite.setSize(512, 512);

        final BGFX bgfx = new BGFX();
        
        Button b = new Button(shell, SWT.NONE);
            b.setLocation(550, 550);
            b.setSize(150, 50);
            b.setText("Click me!");
        
            b.addSelectionListener(new SelectionListener() {
                
                public void widgetSelected(SelectionEvent e) {
                    if(composite.getSize().x < 1024) {
                        composite.setSize(1024, 1024);
                    } else {
                        composite.setSize(512, 512);
                    }
                    
                    bgfx.reset(1024, 1024, BGFX.BGFX_RESET_NONE);
                }
                
                public void widgetDefaultSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub
                    
                }
            });

        shell.open();
            
        BGFXHelper.setupBGFX(bgfx, composite);
        bgfx.init();   

        bgfx.reset(512, 512, BGFX.BGFX_RESET_NONE);
        bgfx.setDebug(BGFX.BGFX_DEBUG_STATS);
        bgfx.setViewName(0, "test");
        
        while(!shell.isDisposed()) {
            if(!display.readAndDispatch()) {
                display.sleep();
            }
           
            render();
            
            bgfx.frame();
        }
        
    }

    private static void render() {
        // TODO Auto-generated method stub
        
    }

}
