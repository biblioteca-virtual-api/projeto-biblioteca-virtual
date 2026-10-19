package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.Autor;
import br.edu.christus.backend.domain.Genero;
import br.edu.christus.backend.domain.Livro;
import br.edu.christus.backend.dto.LivroRequestDTO;
import br.edu.christus.backend.dto.LivroResponseDTO;
import br.edu.christus.backend.exception.BusinessException;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.AutorRepository;
import br.edu.christus.backend.repository.GeneroRepository;
import br.edu.christus.backend.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository repository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Transactional
    public LivroResponseDTO create(LivroRequestDTO dto) {
        if (repository.existsByIsbn(dto.isbn())) {
            throw new ConflictException("Já existe um livro com o ISBN " + dto.isbn());
        }
        Livro livro = new Livro();
        preencher(livro, dto);
        return toResponse(repository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO findById(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public LivroResponseDTO update(Long id, LivroRequestDTO dto) {
        Livro livro = buscar(id);
        if (repository.existsByIsbnAndIdNot(dto.isbn(), id)) {
            throw new ConflictException("Já existe outro livro com o ISBN " + dto.isbn());
        }
        preencher(livro, dto);
        return toResponse(repository.save(livro));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(buscar(id));
    }

    private Livro buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado. Id: " + id));
    }

    // Copia os dados do DTO para a entidade aplicando RN14 e RN12 (create e update).
    private void preencher(Livro livro, LivroRequestDTO dto) {
        Autor autor = autorRepository.findById(dto.autorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado. Id: " + dto.autorId()));
        Genero genero = generoRepository.findById(dto.generoId())
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado. Id: " + dto.generoId()));

        // RN12: o gênero precisa estar ATIVO
        if (!Boolean.TRUE.equals(genero.getAtivo())) {
            throw new BusinessException("O gênero '" + genero.getName() + "' está inativo e não pode receber livros.");
        }

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setAnoPublicacao(dto.anoPublicacao());
        livro.setSinopse(dto.sinopse());
        livro.setAutor(autor);
        livro.setGenero(genero);
    }

    // Conversão manual: o Genero usa id_genero/name, o que atrapalharia o ModelMapper.
    private LivroResponseDTO toResponse(Livro l) {
        return new LivroResponseDTO(
                l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(), l.getSinopse(),
                l.getAutor().getId(), l.getAutor().getNome(),
                l.getGenero().getId_genero(), l.getGenero().getName());
    }
}
