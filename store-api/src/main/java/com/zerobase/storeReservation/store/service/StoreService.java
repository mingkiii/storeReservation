package com.zerobase.storeReservation.store.service;

import static com.zerobase.storeReservation.store.exception.ErrorCode.SAME_STORE_NAME;

import com.zerobase.storeReservation.store.domain.AddStoreForm;
import com.zerobase.storeReservation.store.domain.model.Store;
import com.zerobase.storeReservation.store.domain.repository.StoreRepository;
import com.zerobase.storeReservation.store.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Store addStore(Long partnerId, AddStoreForm form) {
        Optional<Store> existingStore = storeRepository.findByPartnerIdAndName(partnerId, form.getName());
        if (existingStore.isPresent()) {
            throw new CustomException(SAME_STORE_NAME);
        }
        return  storeRepository.save(Store.of(partnerId, form));
    }
}
