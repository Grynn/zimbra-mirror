/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
using System.Collections.Generic;
using System.Text;
using System.Windows.Controls.Primitives;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media.Imaging;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows;
using System;

namespace MVVM.View.CTI
{
public class CloseableTabItem: TabItem
{
    static CloseableTabItem()
    {
        // This OverrideMetadata call tells the system that this element wants to provide a style that is different than its base class.
        // This style is defined in themes\generic.xaml
        DefaultStyleKeyProperty.OverrideMetadata(typeof (CloseableTabItem),
            new FrameworkPropertyMetadata(typeof (CloseableTabItem)));
    }

    public static readonly RoutedEvent CloseTabEvent = EventManager.RegisterRoutedEvent(
        "CloseTab", RoutingStrategy.Bubble, typeof (RoutedEventHandler),
        typeof (CloseableTabItem));
    public event RoutedEventHandler CloseTab {
        add { AddHandler(CloseTabEvent, value); }
        remove { RemoveHandler(CloseTabEvent, value); }
    }
    public override void OnApplyTemplate()
    {
        base.OnApplyTemplate();

        Button closeButton = base.GetTemplateChild("PART_Close") as Button;

        if (closeButton != null)
            closeButton.Click += new System.Windows.RoutedEventHandler(closeButton_Click);
    }

    void closeButton_Click(object sender, System.Windows.RoutedEventArgs e)
    {
        this.RaiseEvent(new RoutedEventArgs(CloseTabEvent, this));
    }
}
}
