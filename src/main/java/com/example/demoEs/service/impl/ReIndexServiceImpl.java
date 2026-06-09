package com.example.demoEs.service.impl;

import com.example.demoEs.es.document.DataPreventES;
import com.example.demoEs.es.document.PreventES;
import com.example.demoEs.es.repository.db.DataPreventRepository;
import com.example.demoEs.model.DataPrevent;
import com.example.demoEs.service.ReIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của {@link ReIndexService} cho PreventES documents.
 *
 * <p>Data flow: MySQL (DataPrevent) → Transform → Elasticsearch (PreventES)
 *
 * <p>Template method được kế thừa từ {@link AbstractReIndexService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReIndexServiceImpl implements ReIndexService {

  private final DataPreventRepository dataPreventRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  @Override
  public void reIndexDataPrevent(List<String> idPrevents) {
    new PreventReIndexTask().reIndex(idPrevents);
  }

  /**
   * Inner class: Template Method cho PreventES reindex.
   *
   * <p>Nguồn: MySQL (DataPrevent) qua JPA repository.
   * Đích: Elasticsearch index {@code prevent_index}.
   */
  private class PreventReIndexTask extends AbstractReIndexService<DataPrevent, PreventES, String> {

    public PreventReIndexTask() {
      super(ReIndexServiceImpl.this.elasticsearchOperations);
    }

    @Override
    protected String getSourceIndex() {
      return "prevent_index";
    }

    @Override
    protected String getTargetIndex() {
      return "prevent_index";
    }

    @Override
    protected PreventES transform(DataPrevent source) {
      PreventES target = new PreventES();
      target.setId(source.getUuid());

      DataPreventES dataEs = new DataPreventES();
      BeanUtils.copyProperties(source, dataEs);
      target.setDataPreventES(dataEs);

      // TODO: join từ Property table để build personalsES và organizationsES
      target.setPersonalsES(new ArrayList<>());
      target.setOrganizationsES(new ArrayList<>());

      return target;
    }

    @Override
    protected Class<DataPrevent> getSourceClass() {
      return DataPrevent.class;
    }

    @Override
    protected CrudRepository<DataPrevent, String> getJpaRepository() {
      return dataPreventRepository;
    }
  }
}
