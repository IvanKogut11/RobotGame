package main.java.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import main.java.log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(getClosingWindowListener(this, x -> System.exit(0)));
    }


    private GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        gameWindow.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        gameWindow.addInternalFrameListener(getClosingInternalFrameListener(gameWindow, x -> x.dispose()));
        return gameWindow;
    }


    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        logWindow.addInternalFrameListener(getClosingInternalFrameListener(logWindow, x -> x.dispose()));
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }

    private JMenuItem getSystemScheme() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem getSpecifiedMenuItem(String title, int keyEvent, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(title, keyEvent);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    private JMenu getSpecifiedMenu(String title, int mnemonicKeyEvent, String accessibleDescription) {
        JMenu resMenu = new JMenu(title);
        resMenu.setMnemonic(mnemonicKeyEvent);
        resMenu.getAccessibleContext().setAccessibleDescription(
                accessibleDescription);
        return resMenu;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = getSpecifiedMenu("Режим отображения", KeyEvent.VK_V,
                "Управление режимом отображения приложения");
        lookAndFeelMenu.add(getSystemScheme());
        JMenuItem genericScheme = getSpecifiedMenuItem("Универсальная схема", KeyEvent.VK_S,
                (event) -> {
                    setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    this.invalidate();
                });
        lookAndFeelMenu.add(genericScheme);

        JMenu testMenu = getSpecifiedMenu("Тесты", KeyEvent.VK_T,
                "Тестовые команды");
        JMenuItem addLogMessageItem = getSpecifiedMenuItem("Сообщение в лог", KeyEvent.VK_S,
                (event) -> {
                    Logger.debug("Новая строка");
                });
        testMenu.add(addLogMessageItem);

        JMenu exitMenu = getSpecifiedMenu("Выход", KeyEvent.VK_ESCAPE, "Кнопка для выхода");
        exitMenu.addMenuListener(getClosingMenuListener(exitMenu, x -> System.exit(0)));

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(exitMenu);
        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private WindowListener getClosingWindowListener(JFrame frame, Consumer<JFrame> closingConsumer) {
        return new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Do you want to quit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION)
                    closingConsumer.accept(frame);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
    }

    private InternalFrameListener getClosingInternalFrameListener(JInternalFrame internalFrame, Consumer<JInternalFrame> closingConsumer) {
        return new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Do you want to quit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION)
                    closingConsumer.accept(internalFrame);
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
    }

    private MenuListener getClosingMenuListener(JMenu menu, Consumer<JMenu> closingConsume) {
        return new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Do you want to quit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION)
                    closingConsume.accept(menu);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
    }
}
