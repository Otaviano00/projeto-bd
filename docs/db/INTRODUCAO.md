Participantes
- Kauã Otaviano Teixeira - 251023550
- Caio de Castro Araujo - 251019323
- Daniel de Oliveira Morais - 242042396
- João Guilherme Cavalcanti Machado - 251023110

# 1. Introdução

O RPG (_Role-Playing Game_) é um jogo de interpretação colaborativo no qual os participantes criam personagens e constroem narrativas de forma livre, sem roteiros ou gráficos predefinidos. O rumo da história é dinâmico, sendo definido pelas decisões dos jogadores em tempo real.

Para que essa dinâmica funcione, destaca-se a figura do Mestre, que atua como o motor do jogo: ele é responsável por estruturar o universo, garantir a consistência das regras e interpretar todos os elementos não controlados diretamente pelos jogadores, como cenários, monstros e NPCs (_Non-Player Characters_).

# 2. Motivo da Escolha do Tema

O gerenciamento de uma narrativa de RPG gera um volume expressivo de dados interconectados que precisam ser armazenados, consultados e atualizados constantemente. Além de aplicar de forma prática diversos conceitos fundamentais apresentados em sala de aula — como modelagem conceitual, restrições de integridade, álgebra relacional e programação em SGBD —, o sistema proposto visa centralizar e organizar essas informações para aprimorar a experiência dos participantes. A plataforma permite registrar o histórico de sessões passadas, planejar campanhas ativas e gerenciar o ecossistema de regras e fichas de forma flexível.

# 3. Estrutura e Principais Entidades

A arquitetura do banco de dados foi concebida para suportar diferentes sistemas de regras e características variadas de fichas, estruturando-se sobre as seguintes entidades e conceitos fundamentais:

### 3.1 Descrição das entidades

- **Usuários:** Participantes cadastrados no sistema. Um usuário pode atuar flexivelmente como Mestre em uma campanha e como Jogador em outra.
    
- **Campanhas:** As histórias e universos principais conduzidos por um Mestre, englobando todo o escopo da aventura.
    
- **Personagens:** Avatares criados e interpretados pelos jogadores para interagir com o mundo da campanha.
    
- **Sessões:** Os encontros cronológicos onde mestres e jogadores se reúnem para jogar, compondo o histórico da campanha.
    
- **Sistemas de Regras:** O conjunto customizável de mecânicas, estatísticas e parâmetros (como os livros de regras) escolhido para reger e estruturar o funcionamento das campanhas e fichas.
    
- **Itens:** Recursos, equipamentos e espólios gerenciados e utilizados dentro do ecossistema do sistema de jogo.
    
- **NPCs (_Non-Player Characters_):** Personagens controlados pelo Mestre que povoam o mundo e movimentam a narrativas.
    
- **Monstros:** Criaturas, oponentes e antagonistas desafiadores gerenciados pelo Mestre e enfrentados ao longo das aventuras e campanhas.
    
- **Cenários:** Os locais, ambientes e mapas geográficos onde as campanhas e sessões ocorrem.
    
- **Anotações:** Registros particulares, diários de bordo e anotações de enredo dependentes de cada usuário no sistema.

- **Templates de fichas:** Modelos de ficha pertencentes a cada sistema de regras. Eles são utilizados posteriormente na criação e preenchimento das fichas concretas de personagens, NPCs, monstros e itens.

# 4. Ferramentas utilizadas

- Draw.io: Utilizado na criação do DER/MER
- MySQL Workbench: Utilizado na criação do MR e do script do banco

# 5. Uso de Inteligência Artificial

Ferramentas de inteligência artificial foram empregadas na revisão dos artefatos gerados e na estruturação da documentação.
