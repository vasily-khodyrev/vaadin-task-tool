package com.alu.tat.view.menu;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/23/2016
 */
public class PopupMenuManager {
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
        window.setContent(content);
        window.setModal(false);
        window.setWidth("300px");
        window.setHeight("150px");
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
        public void setWindow(Window w);
    }
}
