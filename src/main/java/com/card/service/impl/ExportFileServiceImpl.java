package com.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.card.command.exportfile.ExportFileCommand;
import com.card.dao.ExportFileDao;
import com.card.entity.domain.ExportFile;
import com.card.service.ExportFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExportFileServiceImpl implements ExportFileService {
    @Autowired
    private ExportFileDao exportFileDao;

    @Override
    public IPage<ExportFile> findByPage(Integer pageNum, Integer pageSize, ExportFileCommand command) {
        Page<ExportFile> productPage = new Page<>(pageNum, pageSize);
        QueryWrapper<ExportFile> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(command.getFileName())) {
            wrapper.like("file_name", command.getFileName());
        }
        if (command.getStartTime() != null && command.getEndTime() != null) {
            wrapper.between("create_time", command.getStartTime(), command.getEndTime());
        }
        return exportFileDao.selectPage(productPage, wrapper);
    }
}