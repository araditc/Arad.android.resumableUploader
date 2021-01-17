package com.araditc.uploader.data.databse;

import com.araditc.uploader.struct.AradUploadDbStruct;
import com.araditc.uploader.struct.UploadHistoryDbStruct;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {AradUploadDbStruct.class, UploadHistoryDbStruct.class})
public class UploadModule {
}
