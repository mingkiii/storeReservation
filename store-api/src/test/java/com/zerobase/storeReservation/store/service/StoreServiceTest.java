package com.zerobase.storeReservation.store.service;

import static com.zerobase.storeReservation.store.exception.ErrorCode.SAME_STORE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import com.zerobase.storeReservation.store.domain.form.AddStoreForm;
import com.zerobase.storeReservation.store.domain.model.Store;
import com.zerobase.storeReservation.store.domain.repository.StoreRepository;
import com.zerobase.storeReservation.store.exception.CustomException;
import com.zerobase.storeReservation.store.util.GeocodingUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

@SpringBootTest
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private GeocodingUtil geocodingUtil;

    @Test
    @DisplayName("상점 등록 성공")
    public void addStore() {
        // Given
        Long partnerId = 1L;
        String storeName = "TestStore";
        AddStoreForm form = AddStoreForm.builder()
            .name(storeName)
            .address("서울시 강남구")
            .build();
        double[] coords = {37.12345, 127.98765};

        when(storeRepository.findByPartnerIdAndName(partnerId, storeName))
            .thenReturn(Optional.empty());
        when(geocodingUtil.geoCoding(form.getAddress())).thenReturn(coords);
        Store expectedStore = Store.of(partnerId, form, coords[0], coords[1]);
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
        CustomException exception = assertThrows(CustomException.class,
            () -> storeService.addStore(partnerId, form));

        // Then
        assertEquals(SAME_STORE_NAME, exception.getErrorCode());
        verify(storeRepository, times(1)).findByPartnerIdAndName(partnerId,
            form.getName());
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    public void testGetNearbyStoresOrderByDistance() {
        // Given
        String address = "인계동 1110";
        double maxDistance = 10000;
        int page = 1;
        int pageSize = 10;
        Sort sort = Sort.by("distance").ascending();

        List<StoreDto> fakeStores = Arrays.asList(
            StoreDto.builder()
                .name("Store 1")
                .distance(100)
                .build(),
            StoreDto.builder()
                .name("Store 2")
                .distance(200)
                .build(),
            StoreDto.builder()
                .name("Store 3")
                .distance(300)
                .build()
        );

        when(geocodingUtil.geoCoding(address)).thenReturn(new double[2]);
        when(storeService.getNearbyStores(address,
            maxDistance, page, pageSize, sort))
            .thenReturn(new PageImpl<>(fakeStores));

        // When
        Page<StoreDto> result = storeService.getNearbyStores(address,
            maxDistance, page, pageSize, sort);

        // Then
        assertNotNull(result);
        assertEquals(fakeStores.size(), result.getTotalElements());
        assertEquals("Store 1", result.getContent().get(0).getName());
        verify(storeRepository).getNearbyStores(anyDouble(), anyDouble(),
            anyDouble(), anyInt(), anyInt(), any(Sort.class));
    }
}