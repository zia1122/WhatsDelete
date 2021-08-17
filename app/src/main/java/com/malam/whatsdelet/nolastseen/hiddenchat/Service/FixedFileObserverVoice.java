 package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.os.FileObserver;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

 public abstract class FixedFileObserverVoice {
 private final static HashMap<File, Set<FixedFileObserverVoice>> sObserverLists = new HashMap<>();
 
 private FileObserver mObserver;
 private final File mRootPath;
 private final int mMask;
 
 public FixedFileObserverVoice(String path) {this(path, FileObserver.ALL_EVENTS);}
 public FixedFileObserverVoice(String path, int mask) {
     mRootPath = new File(path);
     mMask = mask;
 }
 
 public abstract void onEvent(int event, String path);
 
 public void startWatching() {
     synchronized (sObserverLists) {
         if (!sObserverLists.containsKey(mRootPath)) sObserverLists.put(mRootPath, new HashSet<FixedFileObserverVoice>());
         
         final Set<FixedFileObserverVoice> fixedObservers = sObserverLists.get(mRootPath);
         
         mObserver = fixedObservers.size() > 0 ? fixedObservers.iterator().next().mObserver : new FileObserver(mRootPath.getPath()) {
             @Override public void onEvent(int event, String path) {
                 switch (event){
                     case FileObserver.CREATE:
                         break;
                     case FileObserver.ACCESS:
                         break;
                     case FileObserver.ATTRIB:
                         break;
                     case FileObserver.CLOSE_NOWRITE:
                         break;
                     case FileObserver.CLOSE_WRITE:
                         break;
                     case FileObserver.DELETE:
                         for (FixedFileObserverVoice fixedObserver : fixedObservers)
                             if ((event & fixedObserver.mMask) != 0)
                                 fixedObserver.onEvent(event, path);
                 
                 break;
                     case FileObserver.DELETE_SELF:
                         for (FixedFileObserverVoice fixedObserver : fixedObservers)
                             if ((event & fixedObserver.mMask) != 0)
                                 fixedObserver.onEvent(event, path);
                             
                             break;
                     case FileObserver.MODIFY:
                         break;
                     case FileObserver.MOVE_SELF:
                         break;
                     case FileObserver.MOVED_FROM:
                         break;
                     case FileObserver.MOVED_TO:
                         for (FixedFileObserverVoice fixedObserver : fixedObservers)
                             if ((event & fixedObserver.mMask) != 0)
                                 fixedObserver.onEvent(event, path);
                         break;
                     case FileObserver.OPEN:
                         break;
                     case FileObserver.ALL_EVENTS:
                         break;
                     default:break;
                 }
                 if (event == FileObserver.DELETE||event == FileObserver.CREATE) {
                     for (FixedFileObserverVoice fixedObserver : fixedObservers)
                         if ((event & fixedObserver.mMask) != 0)
                             fixedObserver.onEvent(event, path);
                 } /*if (event == FileObserver.CREATE) {
                     for (FixedFileObserver fixedObserver : fixedObservers)
                         if ((event & fixedObserver.mMask) != 0)
                             fixedObserver.onEvent(event, path);
                 } if (event == FileObserver.DELETE_SELF) {
                     for (FixedFileObserver fixedObserver : fixedObservers)
                         if ((event & fixedObserver.mMask) != 0)
                             fixedObserver.onEvent(event, path);
                 }*/
             }};
         mObserver.startWatching();
         fixedObservers.add(this);
     }
 }
 
 public void stopWatching() {
     synchronized (sObserverLists) {
         Set<FixedFileObserverVoice> fixedObservers = sObserverLists.get(mRootPath);
         if ((fixedObservers == null) || (mObserver == null)) return;
         
         fixedObservers.remove(this);
         if (fixedObservers.size() == 0) mObserver.stopWatching();
         
         mObserver = null;
     }
 }
 
 protected void finalize() {stopWatching();}
 }

