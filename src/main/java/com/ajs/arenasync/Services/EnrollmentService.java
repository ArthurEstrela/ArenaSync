package com.ajs.arenasync.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Enrollment;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.EnrollmentRepository;
import com.ajs.arenasync.Repositories.TeamRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class EnrollmentService {


