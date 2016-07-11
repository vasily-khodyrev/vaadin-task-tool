package com.alu.tat.view.menu;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/23/2016
 */
public class PopupMenuManager implements Serializable {
    private CopyOnWriteArrayList<Window> extraWindows = new CopyOnWriteArrayList<>();

    public PopupMenuManager(AbstractOrderedLayout layout) {
        layout.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                if (event.getButton().equals(MouseEventDetails.MouseButton.LEFT)) {
                    closeExtraWindows();
                }
            }
        });
    }

    public void showWindow(int x, int y, PopupContent content) {
        closeExtraWindows();

        final Window window = new Window();
        VerticalSplitPanel vl = new VerticalSplitPanel();
        Label l = new Label(content.getTitle());
        l.setSizeUndefined();
        VerticalLayout vert = new VerticalLayout(l);
        vert.setWidth(100, Sizeable.Unit.PERCENTAGE);
        vert.setComponentAlignment(l, Alignment.TOP_CENTER);
        vl.setFirstComponent(vert);
        vl.setSecondComponent(content);
        vl.setSplitPosition(10, Sizeable.Unit.PERCENTAGE);
        window.setContent(vl);
        window.setStyleName("valo-menu", true);
        window.setStyleName("v-window-top-toolbar", true);
        Responsive.makeResponsive(window);
        //window.addStyleName(ValoTheme.UI_WITH_MENU);
        window.setStyleName(ValoTheme.PANEL_BORDERLESS, true);
        content.setSizeFull();
        window.setModal(false);
        window.setWidth("150px");
        window.setHeight("250px");
        window.setPositionX(x);
        window.setPositionY(y);
        window.setClosable(false);
        window.setResizable(false);
        extraWindows.add(window);
        content.setWindow(window);

        UI.getCurrent().addWindow(window);
    }

    private void closeExtraWindows() {
        Iterator<Window> wit = extraWindows.listIterator();
        while (wit.hasNext()) {
            Window w = wit.next();
            w.close();
            extraWindows.remove(wit);
        }
    }

    public interface PopupContent extends Component {
        public String getTitle();
        public void setWindow(Window w);
    }
}
