package com.araditc.uploader.data.databse;


import com.araditc.uploader.AppConfig;
import com.araditc.uploader.struct.AradUploadDbStruct;
import com.araditc.uploader.struct.UploadStruct;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UploadDAO {
    private Realm realm;
    private final String FILE_ID_COLUMN = "fileId";
    private final String SEQUENCE_NUMBER_COLUMN = "sequenceNumber";
    private final String ID_COLUMN = "id";
    private final String STATUS_COLUMN = "status";
    private UploadStruct uploadStruct;

    public UploadDAO() {
        this.realm = AppConfig.farmRealm;
    }

    public void saveChunk(AradUploadDbStruct uploadDbStruct, TransActionResult transActionResult) {
        realm.executeTransaction(realm -> {
            Number currentIdNum = realm.where(AradUploadDbStruct.class).max(ID_COLUMN);
            int nextId;

            if (currentIdNum == null) nextId = 1;
            else nextId = currentIdNum.intValue() + 1;

            AradUploadDbStruct realmObj = realm.createObject(AradUploadDbStruct.class, nextId);
            realmObj.init(uploadDbStruct);

            uploadDbStruct.setId(nextId);

            if (transActionResult == null) return;

            UploadStruct uploadStruct = new UploadStruct();
            uploadStruct.convert(uploadDbStruct);
            transActionResult.onSuccess(uploadStruct);
        });
    }

    public void deleteChunk(UploadStruct uploadStruct, TransActionResult transActionResult) {

        realm.executeTransactionAsync(realm -> {
            AradUploadDbStruct uploadDbStruct = realm.where(AradUploadDbStruct.class)
                    .equalTo(ID_COLUMN, uploadStruct.getId()).findFirst();

            uploadDbStruct.deleteFromRealm();
        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadStruct);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public void updateChunkStatus(String id, int status, TransActionResult transActionResult) {
        realm.executeTransactionAsync(realm -> {
            AradUploadDbStruct uploadDbStruct = realm.where(AradUploadDbStruct.class)
                    .equalTo(ID_COLUMN, id).findFirst();

            uploadDbStruct.setStatus(status);

            uploadStruct = new UploadStruct();
            uploadStruct.convert(uploadDbStruct);

        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadStruct);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public void findChunk(String uploadId, TransActionResult transActionResult) {
        realm.executeTransactionAsync(realm -> {
            AradUploadDbStruct uploadDbStruct = realm.where(AradUploadDbStruct.class)
                    .equalTo(FILE_ID_COLUMN, uploadId).findFirst();

            uploadStruct = new UploadStruct();
            uploadStruct.convert(uploadDbStruct);

        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadStruct);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    List<UploadStruct> uploadStructList;

    public void findChunks(String uploadId, TransActionListResult transActionResult) {
        realm.executeTransactionAsync(realm -> {
            List<AradUploadDbStruct> all = realm.where(AradUploadDbStruct.class)
                    .equalTo(FILE_ID_COLUMN, uploadId).findAll();

            uploadStructList = UploadStruct.convert(all);
        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadStructList);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public RealmResults<AradUploadDbStruct> findAll() {
        return realm.where(AradUploadDbStruct.class).findAll();
    }

    public void close() {
        if (realm != null)
            realm.close();
    }

    public interface TransActionResult {
        void onSuccess(UploadStruct uploadStruct);

        void onError(Throwable error);
    }

    public interface TransActionListResult {
        void onSuccess(List<UploadStruct> structList);

        void onError(Throwable error);
    }
}
