package com.leadlet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.leadlet.service.TimelineService;
import com.leadlet.service.dto.TimelineDTO;
import com.leadlet.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Timeline.
 */
@RestController
@RequestMapping("/api")
public class TimelineResource {

    private final Logger log = LoggerFactory.getLogger(TimelineResource.class);

    private static final String ENTITY_NAME = "timeline";

    private final TimelineService timelineService;

    public TimelineResource(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    /**
     * GET  /timelines : get all the timelines.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of timelines in body
     */
    @GetMapping("/timeLines")
    @Timed
    public ResponseEntity<List<TimelineDTO>> getAllTimelines(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Timelines");
        Page<TimelineDTO> page = timelineService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeLines");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/timeLines/person/{personId}")
    @Timed
    public ResponseEntity<List<TimelineDTO>> getPersonTimelines(@PathVariable Long personId, @ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Timelines for person {}", personId);

        Page<TimelineDTO> page = timelineService.findByPersonId(personId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeLines");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/timeLines/deal/{dealId}")
    @Timed
    public ResponseEntity<List<TimelineDTO>> getDealTimelines(@PathVariable Long dealId, @ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Timelines for deal {}", dealId);

        Page<TimelineDTO> page = timelineService.findByDealId(dealId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeLines");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/timeLines/user/{userId}")
    @Timed
    public ResponseEntity<List<TimelineDTO>> getUserTimelines(@PathVariable Long userId, @ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Timelines for user {}", userId);

        Page<TimelineDTO> page = timelineService.findByUserId(userId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeLines");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}

