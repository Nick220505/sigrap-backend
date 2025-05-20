package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

  @Mock
  private AuditLogRepository auditLogRepository;

  @Mock
  private AuditLogMapper auditLogMapper;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private AuditLogService auditLogService;

  private AuditLog testAuditLog;
  private AuditLogInfo testAuditLogInfo;
  private LocalDateTime now;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();
    pageable = PageRequest.of(0, 10);

    testAuditLog = AuditLog.builder()
      .id(1L)
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .timestamp(now)
      .sourceIp("192.168.1.1")
      .userAgent("Mozilla/5.0")
      .details("{\"test\":\"data\"}")
      .status("SUCCESS")
      .durationMs(100L)
      .build();

    testAuditLogInfo = AuditLogInfo.builder()
      .id(1L)
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .timestamp(now)
      .sourceIp("192.168.1.1")
      .userAgent("Mozilla/5.0")
      .details("{\"test\":\"data\"}")
      .status("SUCCESS")
      .durationMs(100L)
      .build();
  }

  @Test
  void log_withBasicInfo_savesAuditLog() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);
    when(auditLogMapper.toInfo(any(AuditLog.class))).thenReturn(
      testAuditLogInfo
    );

    AuditLogInfo result = auditLogService.log("testuser", "CREATE", "USER");

    assertNotNull(result);
    assertEquals(testAuditLogInfo.getId(), result.getId());
    assertEquals(testAuditLogInfo.getUsername(), result.getUsername());
    assertEquals(testAuditLogInfo.getAction(), result.getAction());
    assertEquals(testAuditLogInfo.getEntityName(), result.getEntityName());

    verify(auditLogRepository, times(1)).save(any(AuditLog.class));
  }

  @Test
  void log_withEntityId_savesAuditLog() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);
    when(auditLogMapper.toInfo(any(AuditLog.class))).thenReturn(
      testAuditLogInfo
    );

    AuditLogInfo result = auditLogService.log(
      "testuser",
      "CREATE",
      "USER",
      "123"
    );

    assertNotNull(result);
    assertEquals(testAuditLogInfo.getEntityId(), result.getEntityId());
    verify(auditLogRepository, times(1)).save(any(AuditLog.class));
  }

  @Test
  void log_withAllDetails_savesAuditLog() {
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);
    when(auditLogMapper.toInfo(any(AuditLog.class))).thenReturn(
      testAuditLogInfo
    );

    AuditLogInfo result = auditLogService.log(
      "testuser",
      "CREATE",
      "USER",
      "123",
      "192.168.1.1",
      "Mozilla/5.0",
      "{\"test\":\"data\"}"
    );

    assertNotNull(result);
    assertEquals(testAuditLogInfo.getSourceIp(), result.getSourceIp());
    assertEquals(testAuditLogInfo.getUserAgent(), result.getUserAgent());
    assertEquals(testAuditLogInfo.getDetails(), result.getDetails());
    verify(auditLogRepository, times(1)).save(any(AuditLog.class));
  }

  @Test
  void publishAuditEvent_publishesEvent() {
    AuditEvent event = AuditEvent.builder()
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .build();
    auditLogService.publishAuditEvent(event);

    verify(eventPublisher, times(1)).publishEvent(event);
  }

  @Test
  void handleAuditEvent_savesAuditLog() {
    AuditEvent event = AuditEvent.builder()
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .build();
    when(auditLogMapper.fromEvent(any(AuditEvent.class))).thenReturn(
      testAuditLog
    );

    auditLogService.handleAuditEvent(event);

    verify(auditLogRepository, times(1)).save(any(AuditLog.class));
  }

  @Test
  void findAll_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(auditLogRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(
      auditLogPage
    );
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findAll(pageable);

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findAllByOrderByTimestampDesc(
      pageable
    );
  }

  @Test
  void findByUsername_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByUsernameOrderByTimestampDesc(
        "testuser",
        pageable
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByUsername(
      "testuser",
      pageable
    );

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findByUsernameOrderByTimestampDesc(
      "testuser",
      pageable
    );
  }

  @Test
  void findByAction_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByActionOrderByTimestampDesc("CREATE", pageable)
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByAction(
      "CREATE",
      pageable
    );

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findByActionOrderByTimestampDesc(
      "CREATE",
      pageable
    );
  }

  @Test
  void findByEntityName_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByEntityNameOrderByTimestampDesc("USER", pageable)
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByEntityName(
      "USER",
      pageable
    );

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findByEntityNameOrderByTimestampDesc(
      "USER",
      pageable
    );
  }

  @Test
  void findByEntityId_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByEntityIdOrderByTimestampDesc("123", pageable)
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByEntityId("123", pageable);

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findByEntityIdOrderByTimestampDesc(
      "123",
      pageable
    );
  }

  @Test
  void findByEntityNameAndEntityId_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(
        eq("USER"),
        eq("123"),
        any(Pageable.class)
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByEntityNameAndEntityId(
      "USER",
      "123",
      pageable
    );

    assertEquals(expectedPage, result);
    verify(
      auditLogRepository,
      times(1)
    ).findByEntityNameAndEntityIdOrderByTimestampDesc("USER", "123", pageable);
  }

  @Test
  void findByDateRange_returnsPageOfAuditLogInfo() {
    LocalDateTime start = now.minusDays(1);
    LocalDateTime end = now.plusDays(1);

    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(
        eq(start),
        eq(end),
        any(Pageable.class)
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findByDateRange(
      start,
      end,
      pageable
    );

    assertEquals(expectedPage, result);
    verify(
      auditLogRepository,
      times(1)
    ).findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
  }

  @Test
  void findBySourceIp_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findBySourceIpOrderByTimestampDesc(
        "192.168.1.1",
        pageable
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findBySourceIp(
      "192.168.1.1",
      pageable
    );

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findBySourceIpOrderByTimestampDesc(
      "192.168.1.1",
      pageable
    );
  }

  @Test
  void findErrors_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByStatusNotOrderByTimestampDesc(
        "SUCCESS",
        pageable
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findErrors(pageable);

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findByStatusNotOrderByTimestampDesc(
      "SUCCESS",
      pageable
    );
  }

  @Test
  void findById_returnsAuditLogInfo() {
    when(auditLogRepository.findById(1L)).thenReturn(Optional.of(testAuditLog));
    when(auditLogMapper.toInfo(testAuditLog)).thenReturn(testAuditLogInfo);

    AuditLogInfo result = auditLogService.findById(1L);

    assertEquals(testAuditLogInfo, result);
    verify(auditLogRepository, times(1)).findById(1L);
  }

  @Test
  void findById_throwsEntityNotFoundExceptionWhenNotFound() {
    when(auditLogRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      auditLogService.findById(999L);
    });
  }

  @Test
  void findBySpecification_returnsPageOfAuditLogInfo() {
    Specification<AuditLog> spec = mock(Specification.class);
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(auditLogRepository.findAll(spec, pageable)).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result = auditLogService.findBySpecification(
      spec,
      pageable
    );

    assertEquals(expectedPage, result);
    verify(auditLogRepository, times(1)).findAll(spec, pageable);
  }

  @Test
  void findRecentActionsByUser_returnsListOfAuditLogInfo() {
    List<AuditLog> auditLogs = List.of(testAuditLog);
    List<AuditLogInfo> expectedList = List.of(testAuditLogInfo);

    when(
      auditLogRepository.findRecentActionsByUser(
        anyString(),
        any(Pageable.class)
      )
    ).thenReturn(auditLogs);
    when(auditLogMapper.toInfoList(auditLogs)).thenReturn(expectedList);

    List<AuditLogInfo> result = auditLogService.findRecentActionsByUser(
      "testuser",
      pageable
    );

    assertEquals(expectedList, result);
  }

  @Test
  void countActionsByType_returnsListOfCounts() {
    List<Object[]> expectedCounts = new ArrayList<>();
    expectedCounts.add(new Object[] { "CREATE", 10L });

    LocalDateTime start = now.minusDays(7);
    LocalDateTime end = now;

    when(auditLogRepository.countActionsByType(start, end)).thenReturn(
      expectedCounts
    );

    List<Object[]> result = auditLogService.countActionsByType(start, end);

    assertEquals(expectedCounts, result);
  }

  @Test
  void findByEntityNameAndTimestampBetween_returnsPageOfAuditLogInfo() {
    String entityName = "USER";
    LocalDateTime startTime = now.minusDays(7);
    LocalDateTime endTime = now;
    Page<AuditLog> auditLogPage = new PageImpl<>(List.of(testAuditLog));
    Page<AuditLogInfo> expectedPage = new PageImpl<>(List.of(testAuditLogInfo));

    when(
      auditLogRepository.findByEntityNameAndTimestampBetweenOrderByTimestampDesc(
        eq(entityName),
        eq(startTime),
        eq(endTime),
        eq(pageable)
      )
    ).thenReturn(auditLogPage);
    when(auditLogMapper.toInfoPage(auditLogPage)).thenReturn(expectedPage);

    Page<AuditLogInfo> result =
      auditLogService.findByEntityNameAndTimestampBetween(
        entityName,
        startTime,
        endTime,
        pageable
      );

    assertEquals(expectedPage, result);
    verify(
      auditLogRepository
    ).findByEntityNameAndTimestampBetweenOrderByTimestampDesc(
      entityName,
      startTime,
      endTime,
      pageable
    );
  }
}
