package com.alu.tat.service;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.dao.BaseDao;

import java.util.Collection;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/23/2016
 */
public class FolderService {

    public static void createFolder(Folder u) {
        BaseDao.create(u);
    }

    public static Folder getFolderById(Long id) {
        return BaseDao.getById(id, Folder.class);
    }

    public static Collection<Folder> getFolders() {
        return BaseDao.getAll(Folder.class);
    }

    public static void updateFolder(Folder folder) {
        BaseDao.update(folder);
    }

    public static void removeFolder(Long id) {
        BaseDao.removeById(id, Folder.class);
    }
}
