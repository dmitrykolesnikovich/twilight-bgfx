#include "bgfx.h"
#include "bgfxplatform.h"
#include "twilight_bgfx_PlatformUtil.h"
#include <gdk/gdkx.h>
#include <gtk/gtk.h>
#include <stdio.h>

void setPlatformWindow(void* ndt, void* nwh, void* context) {
	GtkWidget* widget = (GtkWidget*)nwh;
	GdkWindow* window = gtk_widget_get_window(GTK_WIDGET(widget));

	if(!widget) {
		return;
	}

	if(!window) {
		return;
	}
	
	Display* display = GDK_WINDOW_XDISPLAY(window);
	
	
	if(!display) {
		return;
	}
	
	Window xid = GDK_WINDOW_XWINDOW(window);

        bgfx::PlatformData pd;
                pd.ndt          = (void*)display;
                pd.nwh          = (void*)xid;
                pd.context      = context;
                pd.backBuffer   = NULL;
                pd.backBufferDS = NULL;

        bgfx::setPlatformData(pd);
}


void JNICALL Java_twilight_bgfx_util_PlatformUtil_nsetPlatformWindow(JNIEnv* env, jobject self, jlong ndt, jlong nwh, jlong context) {
    setPlatformWindow((void*)ndt, (void*)nwh, (void*)context);
}

