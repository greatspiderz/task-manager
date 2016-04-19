package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.SubjectDaoImpl;
import com.github.greatspiderz.tasks.manager.db.model.entities.Subject;
import com.github.greatspiderz.tasks.manager.dto.SearchSubjectDto;

import java.util.List;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
@ImplementedBy(SubjectDaoImpl.class)
public interface SubjectDao extends BaseDao<Subject> {

    List<Subject> searchSubjects(SearchSubjectDto searchSubjectDto);

}
