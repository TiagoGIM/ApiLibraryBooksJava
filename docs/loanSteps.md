# Etapas de criação do Loan

## Criação do controler

1- criar classe controler e classe de teste.
2- criar LoanDto.
3- criar o service e a entity. (LoanService e Loan) //criar uma interface pro service.
4- criar um repository

### Testes
>> Teste de criar emprestipo pr isbm <<
1. CreateLoan
Busca o livro por isbm, e salva livro junto ao usuario em uma tabela relacionada.
2. Tabela Loans

Esta view relaciona o book a um costumer.
-- costumer
-- livro
-- data de emprestimo
-- status do emprestimo (devolvido ou Não devolvido)

Cenário:  dado um isbm chame um serviço para encontrar esse book e outro para criar um emprestimo. 
- BDD para iBookservice e iLoanservice
- dtoMockBook
- dtoMockCostumer
Foi necessario:
criar um metodo ao IBookService para buscar por isbm.

Após a primeira rodade de teste que ira falhar pos não exist rota de post, então implementamos o post seguindo o teste.

1. ele deve usar o book service para pegar um book by isbn.
2. salvar o loan no repository do loan.

>>Teste de recusar emprestimo de ibsm inexistente<<
teste similar ao de criação porém com retornos vazios, primeiro erro doi devido ao post não tratar erros.
-- criar tratamento de erros.  orElseThrow()
-- implementar o @requestAdivice para enviar erros por json.

1. Para lidar com os erros foi criada uma classe para centralizar os Handlers de excpetions. 
Criamos essa classe dentro da api geral, recortamos as excpetions que estavam alocadas em BookControler.
Neste passo tambem criamos mais uma exception nova pra ResponseEntity do LoanController, para isso
criamos outro handler em ApiErros

## Bugs & fixs

Ao tentar usar um objeto para mockar um LoanDto no LoanControllerTest como foi feito no BoookControllerTest o teste não passou. Passsei um Builder e rolou.