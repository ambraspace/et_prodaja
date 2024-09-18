package com.ambraspace.etprodaja.model.preview;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ambraspace.etprodaja.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class PreviewController
{

	@Autowired
	private PreviewService previewService;


	@Operation(summary = "Upload files for product previews",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = {
							@Content(encoding = @Encoding(name = "files", contentType = MediaType.MULTIPART_FORM_DATA_VALUE))
					}
			),
			responses = {
			@ApiResponse(responseCode = "200", description = "Files uploaded and Preview objects returned", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Preview.class)))
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@SecurityRequirement(name = "JWT")
	@RolesAllowed({"ADMIN", "USER"})
	@PostMapping(path = "/api/previews", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public List<Preview> addPreviews(
			@RequestPart(name = "files", required = true) List<MultipartFile> files)
	{
		try
		{
			return previewService.addPreviews(files);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}



	@Operation(summary = "Download product preview", responses = {
			@ApiResponse(responseCode = "200", description = "File returned", content = {
					@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
			}),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(mediaType = "application/json", schema =
							@Schema(implementation = ErrorResponse.class))
			})
	})
	@GetMapping("/api/images/{fn}")
	public void downloadProductPreview(@PathVariable("fn") String fileName, HttpServletResponse response)
	{
		try
		{
			previewService.downloadProductImage(fileName, response);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}





}
