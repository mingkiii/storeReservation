package com.zerobase.storeReservation.store.service;

import static com.zerobase.storeReservation.store.exception.ErrorCode.SAME_STORE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.store.domain.AddStoreForm;
import com.zerobase.storeReservation.store.domain.model.Store;
import com.zerobase.storeReservation.store.domain.repository.StoreRepository;
import com.zerobase.storeReservation.store.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Test
    @DisplayName("상점 등록 성공")
    public void addStore() {
        // Given
        Long partnerId = 1L;
        String storeName = "TestStore";
        AddStoreForm form = AddStoreForm.builder()
            .name(storeName)
            .build();

        when(storeRepository.findByPartnerIdAndName(partnerId, storeName))
            .thenReturn(Optional.empty());
        Store expectedStore = Store.of(partnerId, form);
        when(storeRepository.save(any(Store.class)))
            .thenReturn(expectedStore);

        // When
        Store result = storeService.addStore(partnerId, form);

        // Then
        assertNotNull(result);
        assertEquals(expectedStore, result);
        assertEquals(partnerId, result.getPartnerId());
        assertEquals(storeName, result.getName());
        verify(storeRepository).findByPartnerIdAndName(partnerId, storeName);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("상점 등록 실패-상점명 중복")
    public void addStore_fail() {
        // Given
        Long partnerId = 1L;
        String storeName = "TestStore";
        AddStoreForm form = AddStoreForm.builder()
            .name(storeName)
            .build();
        Store existingStore =
            Store.builder()
                .partnerId(partnerId)
                .name(storeName)
                .build();

        when(storeRepository.findByPartnerIdAndName(partnerId, form.getName()))
            .thenReturn(Optional.of(existingStore));

        // When
        CustomException exception = assertThrows(CustomException.class, () -> storeService.addStore(partnerId, form));

        // Then
        assertEquals(SAME_STORE_NAME, exception.getErrorCode());
        verify(storeRepository, times(1)).findByPartnerIdAndName(partnerId, form.getName());
        verify(storeRepository, never()).save(any(Store.class));
    }
}