package hyundai.softeer.orange.event.common.repository;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventFrameRepositoryDefaultImpl implements CustomEventMetadataRepository {
    private final EntityManager em;

    @Override
    public List<EventMetadata> findAllBySearch(String search, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // 쿼리 객체 생성
        CriteriaQuery<EventMetadata> cq = cb.createQuery(EventMetadata.class);
        // 루트 객체 생성 = Q 객체와 유사한 역할인듯?
        Root<EventMetadata> metadata = cq.from(EventMetadata.class);

        // 정렬 기준이 존재할
        if(search != null && !search.isEmpty()) {
            Predicate condName = cb.like(metadata.get("name"), "%" + search + "%");
            Predicate condEventId = cb.like(metadata.get("eventId"), "%" + search + "%");
            cq.where(condName, condEventId);
        }

        // https://stackoverflow.com/questions/49463512/transform-from-pageable-getsort-to-listorder-to-sort-a-query-made-by-criteri
        cq.orderBy(QueryUtils.toOrders(pageable.getSort(), metadata, cb)).select(metadata);

        TypedQuery<EventMetadata> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}
