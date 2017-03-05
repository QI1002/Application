package io.github.qi1002.ilearn;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}
//TODO00: how to do blocking wait in UI thread smartly.
//TODO01: link the settings with real behaviors and let mainactivity settings work also in settings
//TODO02: resume practice
//TODO03: solve the different jdkName issue in app/app.iml
//TODO04: check if more rules are added to .gitignore
//TODO05: more field in dataset XML like mean_score, listen_score, class, ...
//TODO06: reduce timestamp to second unit
//TODO07: add enumerate way about (timestamp, score mean, voice and class)
//TODO08: update qi1002.github.io and github self icon
//TODO09: handle network go out case
//TODO10: get android simulator in T420
//TODO11: how to save dataset when application finish once. (join and foreground detection ?)
//TODO12: how to wait the checkHTMLsource done then save dataset ?
//TODO13: main settings => score and test frequency, settings (path) and about and utilities
//TODO14: add settings page (download dataset link, default enumator way ...)
//TODO15: About page
//TODO16: wait voiceplay or dataset initialized options (debug)
//TODO17: common 7000 words
//TODO18: dickson idoms
//TODO19:shall we load score history all
//TODO20: move prounication answer to exam: ________
//TODO21: verifyStoragePermissions is enhanced
//TODO22: don't consider no voice case if checking if pron exam ok <=> dataset size
//TODO23: test all data.xml for dictionary feasiblity
//TODO24: change settings icon and bar chart title position depends on screen size
//TODO25: score update by hash set flow => apply to checkWord
//TODO26: enumerate words by category limitation