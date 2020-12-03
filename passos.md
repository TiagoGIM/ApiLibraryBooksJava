# Iniciamos pelos testes

criada a pasta resource que implementa os testes de controllers

iniciamos com as anotações 

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc

## criamos dois testes :
### CreateBookWIthSuccess

1. criar mock do content json.
2. criar mock do request
3. usar mvc pra fzr os assertions

alguns methods do "MockMvcResultMatchers" foram importados staticamente para ficar menos verboso. Basta clicar em cima do method que a opção aparece.

### CreatinvalideBoook

### Teste failed
Como não temos rota implementada o primeiro erro e de status
400, claro, a rota não existe.

## Implementação de rotas
Após criar o primeiro teste completo implementamos a rota nos controllers da main e fomos refatorando o código com base nos testes.

1. criar controller
2. criar DTO

## Testa novamente

Feito o teste o erro foi o tipo de retorno de status novamente, agora devido ao retorno do Post, "null" e deveria ser um object json.

Corrigimos forçando um "created" de status, o que retornou outro erro de "id", então criamos um objeto DTO para isso.

controller testado e funcionando, agora refatoramos o teste para só aceitar json como content.

### refactor test

Criamos um Dto_book e adicionamos um @Builder ao .dto 

construimos um dto_mock e passsamos diretamente para os testes as proprieades do json.

Editamos o post para receber como body um object dto e rodamos novamente.

## implementanto interface de serviços

Criamos uma interface IBookService e mockamos ele com o @MockBean

Criamos um model Book e implementamos um metodo save() na interface retornando um Book usando o mockito.give();

### Test save()

injatemos o service no controller, ele espera receber um Book, sem ID.
Então o salva no banco e em seguida retorna um Book.dto com ID.  usamos o builder para fazer essa interface.

Refatoramos o controler para mapear os dois tipos de Books.
para isso usamos o ModelMapper.

Pelo fato dele não ser gerenciado pelo Spring nos usaremos um @Bean para fazer isso diretamente na aplicação principal. Isso ira criar um Singleton para servir toda a aplicação, da hora né?

## Criando teste para camada de serviços

Similar ao teste controler, criamos a pasta e a classe de teste.
/service/BookServiceTeste

implementamos o o primeiro teste com um service que ainda não foi criado, somente interface, então da erro de nulpointerexception.

Usamos o BeforeEach para setar o serviço antes de cada teste.

# Service

1. criar a classe BookService para implementar a interface.
    @Service da ao springBoot a responabilidade de gerenciar essa camada.
2. Criar o RepositoryBookService.
3. utilizar o mockito para os testes.

@mockito.when()

# Teste de Validação dos dados Controller
Criamos um mock vazio e rodamos, o teste não passou pois o Mapper não aceita objetos vazios.

## Criar validação no DTO.

No file dto adicionamos o @NotEmpty a cada prop.
No controller adicionamos o @valid ao objeto passado na chamada post.

Não deu certo.

Passei pra criação da ApiErros. 

criar handler de api e handler de business
, implementar handler no controler.

