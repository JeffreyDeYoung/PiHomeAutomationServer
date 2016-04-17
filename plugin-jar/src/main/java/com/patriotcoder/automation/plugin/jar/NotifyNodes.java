package com.patriotcoder.automation.plugin.jar;

import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.plugininterfaces.NotifierPlugin;

/**
 * Docussandra plugin that notifies nodes of relevent changes. (Must be placed
 * in the home directory of the Docussandra user.)
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class NotifyNodes extends NotifierPlugin
{

    @Override
    public void doNotify(MutateType type, Document document)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPluginName()
    {
        return "Node-Notifier";
    }

}
