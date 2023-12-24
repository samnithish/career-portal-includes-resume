package com.sam.careerportal;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CandidateController {

	@Autowired
	CandidateRepository canRepo;

	@GetMapping
	public String candidateForm(Model model) {
		Candidate candidate = new Candidate();
		model.addAttribute("candidate", candidate);
		return "Home";
	}

	@PostMapping("/save")
	public String insertCandidate(@RequestParam("candidateName") String name,
			@RequestParam("candidateEmail") String email, @RequestParam("position") String position,
			@RequestParam("resume") MultipartFile resume) throws IOException {

		Candidate can = new Candidate();
		can.setCandidateName(name);
		can.setCandidateEmail(email);
		can.setPosition(position);
		can.setFileName(resume.getOriginalFilename());
		can.setFileType(resume.getContentType());
		can.setResume(resume.getBytes());

		if (canRepo.findByCandidateEmail(email) != null) {
			ResponseEntity.badRequest().body("Email Already Registered");
			return "redirect:/";
		} else {
			canRepo.save(can);
			ResponseEntity.ok("Registered successfully");
			return "redirect:/";
		}
	}

	@GetMapping("/allcandidates")
	public String hrdashboard(Model model) {
		List<Candidate> candidates = canRepo.findAll();
		model.addAttribute("AllCand", candidates);
		return "admin";
	}

	@GetMapping("/downloadresume/{candId:.+}")
	public ResponseEntity<byte[]> downloadResume(@PathVariable Long candId) {
		Candidate candidate = canRepo.findById(candId).get();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(candidate.getFileType()));
		headers.setContentDispositionFormData("attachment", candidate.getFileName());
		return new ResponseEntity<>(candidate.getResume(), headers, HttpStatus.OK);
	}

}
