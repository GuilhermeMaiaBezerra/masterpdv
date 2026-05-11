-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 11/05/2026 às 19:07
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `masterpdv`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_caixa`
--

CREATE TABLE `tb_caixa` (
  `ID_caixa` int(11) NOT NULL,
  `tipo_movimento` varchar(10) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_hora` datetime DEFAULT current_timestamp(),
  `id_usuario` int(11) NOT NULL,
  `forma_pagamento` varchar(30) DEFAULT NULL,
  `id_venda` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_caixa`
--

INSERT INTO `tb_caixa` (`ID_caixa`, `tipo_movimento`, `valor`, `data_hora`, `id_usuario`, `forma_pagamento`, `id_venda`) VALUES
(1, 'Venda', 1034.00, '2026-05-02 12:56:29', 1, 'Dinheiro', 1),
(2, 'Venda', 130.00, '2026-05-07 19:20:39', 1, 'Dinheiro', 8),
(3, 'Venda', 535.00, '2026-05-07 19:20:52', 1, 'PIX', 9),
(4, 'Venda', 860.00, '2026-05-11 15:48:36', 1, 'Crédito', 10),
(5, 'Venda', 940.00, '2026-05-11 15:48:44', 1, 'Débito', 11),
(6, 'Venda', 420.00, '2026-05-11 15:48:49', 1, 'PIX', 12);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_categoria`
--

CREATE TABLE `tb_categoria` (
  `id_categoria` int(11) NOT NULL,
  `nome` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_categoria`
--

INSERT INTO `tb_categoria` (`id_categoria`, `nome`) VALUES
(1, 'Informática'),
(2, 'Periféricos'),
(3, 'Hardware'),
(4, 'Redes'),
(5, 'Smartphones');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_produto`
--

CREATE TABLE `tb_produto` (
  `id_produto` int(11) NOT NULL,
  `nome` varchar(150) NOT NULL,
  `descricao` text DEFAULT NULL,
  `preco` decimal(10,2) NOT NULL,
  `quantidade_estoque` int(11) DEFAULT 0,
  `categoria_id` int(11) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT current_timestamp(),
  `status` int(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_produto`
--

INSERT INTO `tb_produto` (`id_produto`, `nome`, `descricao`, `preco`, `quantidade_estoque`, `categoria_id`, `data_cadastro`, `status`) VALUES
(1, 'Notebook Dell Vostro', 'i5, 16GB RAM, SSD 512GB', 4500.00, 10, 1, '2026-05-02 12:50:30', 1),
(2, 'MacBook Air M2', 'Apple M2, 8GB, 256GB SSD', 8900.00, 5, 1, '2026-05-02 12:50:30', 1),
(3, 'Monitor LG 29 UltraWide', 'Resolução 2560x1080, 75Hz', 1200.00, 15, 1, '2026-05-02 12:50:30', 1),
(4, 'Nobreak SMS 1200VA', 'Entrada bivolt, saída 115V', 850.00, 8, 1, '2026-05-02 12:50:30', 1),
(5, 'Suporte Articulado Monitor', 'Pistão a gás para telas até 32', 250.00, 20, 1, '2026-05-02 12:50:30', 1),
(6, 'Tablet Samsung S9 FE', 'Tela 10.9 polegadas, 128GB', 2100.00, 12, 1, '2026-05-02 12:50:30', 1),
(7, 'Mini PC Intel NUC', 'Core i3, 8GB RAM, sem OS', 1800.00, 4, 1, '2026-05-02 12:50:30', 1),
(8, 'Projetor Epson PowerLite', '3300 Lumens, HDMI', 3200.00, 3, 1, '2026-05-02 12:50:30', 1),
(9, 'Impressora HP LaserJet', 'Laser Monocromática M111w', 980.00, 7, 1, '2026-05-02 12:50:30', 1),
(10, 'Scanner de Mesa Canon', 'Resolução 2400x2400 dpi', 450.00, 6, 1, '2026-05-02 12:50:30', 1),
(11, 'Mouse Logitech MX Master 3S', 'Sensor 8K DPI, Bluetooth', 550.00, 25, 2, '2026-05-02 12:50:30', 1),
(12, 'Teclado Mecânico Keychron K2', 'Switches Gateron Brown', 780.00, 10, 2, '2026-05-02 12:50:30', 1),
(13, 'Headset HyperX Cloud II', 'Som Surround 7.1', 490.00, 17, 2, '2026-05-02 12:50:30', 1),
(14, 'Webcam Logitech C920', 'Full HD 1080p com microfone', 380.00, 30, 2, '2026-05-02 12:50:30', 1),
(15, 'Mousepad Gamer Extra Large', '900x400mm, borda costurada', 89.90, 50, 2, '2026-05-02 12:50:30', 1),
(16, 'Caixa de Som Edifier R1280T', '42W RMS, Monitor de áudio', 750.00, 8, 2, '2026-05-02 12:50:30', 1),
(17, 'Microfone Condensador Blue Yeti', 'Conexão USB para Podcast', 950.00, 5, 2, '2026-05-02 12:50:30', 1),
(18, 'Cabo HDMI 2.1 2 metros', 'Suporte a 8K e 4K 120Hz', 65.00, 99, 2, '2026-05-02 12:50:30', 1),
(19, 'Hub USB-C 7 em 1', 'Saída HDMI, SD e USB 3.0', 180.00, 40, 2, '2026-05-02 12:50:30', 1),
(20, 'Controle Xbox Series Carbon', 'Conexão sem fio e Bluetooth', 420.00, 14, 2, '2026-05-02 12:50:30', 1),
(21, 'Processador AMD Ryzen 7 5700X', '8 núcleos, 16 threads', 1250.00, 12, 3, '2026-05-02 12:50:30', 1),
(22, 'Placa de Vídeo RTX 4060 Ti', '8GB GDDR6, Dual Fan', 2600.00, 7, 3, '2026-05-02 12:50:30', 1),
(23, 'Memória RAM Corsair 16GB', 'DDR4 3200MHz Vengeance', 320.00, 60, 3, '2026-05-02 12:50:30', 1),
(24, 'SSD NVMe Kingston 1TB', 'Leitura 3500MB/s', 410.00, 44, 3, '2026-05-02 12:50:30', 1),
(25, 'Placa Mãe ASUS TUF B550M', 'Socket AM4, suporte PCIe 4.0', 890.00, 14, 3, '2026-05-02 12:50:30', 1),
(26, 'Fonte Corsair RM750e', '750W, 80 Plus Gold, Modular', 680.00, 20, 3, '2026-05-02 12:50:30', 1),
(27, 'Water Cooler Cooler Master', '240mm, Iluminação ARGB', 450.00, 9, 3, '2026-05-02 12:50:30', 1),
(28, 'Gabinete Mid Tower RGB', 'Lateral em vidro temperado', 350.00, 21, 3, '2026-05-02 12:50:30', 1),
(29, 'HD Interno Seagate 2TB', '7200 RPM BarraCuda', 380.00, 30, 3, '2026-05-02 12:50:30', 1),
(30, 'Pasta Térmica Arctic MX-4', 'Seringa de 4g', 45.00, 80, 3, '2026-05-02 12:50:30', 1),
(31, 'Roteador TP-Link Archer AX55', 'Wi-Fi 6 Gigabit Dual Band', 590.00, 20, 4, '2026-05-02 12:50:30', 1),
(32, 'Switch TP-Link 8 Portas', 'Gigabit Ethernet de mesa', 150.00, 35, 4, '2026-05-02 12:50:30', 1),
(33, 'Repetidor Wi-Fi Xiaomi AC1200', 'Dual Band, 1200Mbps', 130.00, 50, 4, '2026-05-02 12:50:30', 1),
(34, 'Placa de Rede Wi-Fi 6E PCIE', 'Bluetooth 5.2 embutido', 220.00, 15, 4, '2026-05-02 12:50:30', 1),
(35, 'Cabo de Rede Cat6 10m', 'Conector RJ45 montado', 45.00, 119, 4, '2026-05-02 12:50:30', 1),
(36, 'Access Point Ubiquiti UniFi', 'U6-Lite Wi-Fi 6', 950.00, 6, 4, '2026-05-02 12:50:30', 1),
(37, 'Modem Roteador 4G/5G', 'Entrada para Chip SIM', 480.00, 10, 4, '2026-05-02 12:50:30', 1),
(38, 'Adaptador USB Ethernet Gigabit', 'Compatível com Nintendo Switch', 85.00, 23, 4, '2026-05-02 12:50:30', 1),
(39, 'Patch Panel 24 Portas', 'Categoria 6 para Rack', 210.00, 5, 4, '2026-05-02 12:50:30', 1),
(40, 'Alicate de Crimpar Profissional', 'Para conectores RJ45/RJ11', 110.00, 11, 4, '2026-05-02 12:50:30', 1),
(41, 'iPhone 15 Pro Max', '256GB, Titânio Natural', 8500.00, 4, 5, '2026-05-02 12:50:30', 1),
(42, 'Samsung Galaxy S24 Ultra', '512GB, com S-Pen', 7200.00, 6, 5, '2026-05-02 12:50:30', 1),
(43, 'Google Pixel 8 Pro', '128GB, Câmera IA', 5400.00, 3, 5, '2026-05-02 12:50:30', 1),
(44, 'Xiaomi Redmi Note 13', '256GB, 8GB RAM', 1650.00, 25, 5, '2026-05-02 12:50:30', 1),
(45, 'Motorola Edge 40', '5G, 256GB, Vegan Leather', 2100.00, 14, 5, '2026-05-02 12:50:30', 1),
(46, 'Carregador Anker 20W USB-C', 'Carga rápida para iPhone/Android', 120.00, 59, 5, '2026-05-02 12:50:30', 1),
(47, 'Cabo USB-C para Lightning 1m', 'Certificado MFi', 89.00, 99, 5, '2026-05-02 12:50:30', 1),
(48, 'Power Bank 20.000mAh', 'Carregamento rápido 22.5W', 250.00, 40, 5, '2026-05-02 12:50:30', 1),
(49, 'Fone Bluetooth Galaxy Buds 2', 'Cancelamento ativo de ruído', 450.00, 19, 5, '2026-05-02 12:50:30', 1),
(50, 'Pelicula Vidro Temperado', 'Kit com 2 unidades universal', 35.00, 200, 5, '2026-05-02 12:50:30', 1);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_usuario`
--

CREATE TABLE `tb_usuario` (
  `ID` int(11) NOT NULL,
  `usuario` varchar(15) NOT NULL,
  `senha` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_usuario`
--

INSERT INTO `tb_usuario` (`ID`, `usuario`, `senha`) VALUES
(1, 'admin', '123'),
(2, 'julia', '123');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_venda`
--

CREATE TABLE `tb_venda` (
  `ID_venda` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `data_hora` datetime DEFAULT current_timestamp(),
  `valor_total` decimal(10,2) NOT NULL,
  `forma_pagamento` varchar(30) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_venda`
--

INSERT INTO `tb_venda` (`ID_venda`, `id_usuario`, `data_hora`, `valor_total`, `forma_pagamento`, `status`) VALUES
(1, 1, '2026-05-02 12:56:29', 1034.00, 'Dinheiro', 'Concluído'),
(8, 1, '2026-05-07 19:20:39', 130.00, 'Dinheiro', 'concluido'),
(9, 1, '2026-05-07 19:20:52', 535.00, 'PIX', 'concluido'),
(10, 1, '2026-05-11 15:48:36', 860.00, 'Crédito', 'concluido'),
(11, 1, '2026-05-11 15:48:44', 940.00, 'Débito', 'concluido'),
(12, 1, '2026-05-11 15:48:49', 420.00, 'PIX', 'concluido');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tb_venda_item`
--

CREATE TABLE `tb_venda_item` (
  `id_item` int(11) NOT NULL,
  `id_venda` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `preco_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `tb_venda_item`
--

INSERT INTO `tb_venda_item` (`id_item`, `id_venda`, `id_produto`, `quantidade`, `preco_unitario`, `subtotal`) VALUES
(1, 1, 38, 1, 85.00, 85.00),
(2, 1, 47, 1, 89.00, 89.00),
(3, 1, 40, 1, 110.00, 110.00),
(4, 1, 16, 1, 750.00, 750.00),
(17, 8, 35, 1, 45.00, 45.00),
(18, 8, 38, 1, 85.00, 85.00),
(19, 9, 18, 1, 65.00, 65.00),
(20, 9, 46, 1, 120.00, 120.00),
(21, 9, 28, 1, 350.00, 350.00),
(22, 10, 27, 1, 450.00, 450.00),
(23, 10, 24, 1, 410.00, 410.00),
(24, 11, 13, 1, 490.00, 490.00),
(25, 11, 49, 1, 450.00, 450.00),
(26, 12, 20, 1, 420.00, 420.00);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `tb_caixa`
--
ALTER TABLE `tb_caixa`
  ADD PRIMARY KEY (`ID_caixa`),
  ADD KEY `fk_caixa_venda` (`id_venda`);

--
-- Índices de tabela `tb_categoria`
--
ALTER TABLE `tb_categoria`
  ADD PRIMARY KEY (`id_categoria`);

--
-- Índices de tabela `tb_produto`
--
ALTER TABLE `tb_produto`
  ADD PRIMARY KEY (`id_produto`);

--
-- Índices de tabela `tb_usuario`
--
ALTER TABLE `tb_usuario`
  ADD PRIMARY KEY (`ID`);

--
-- Índices de tabela `tb_venda`
--
ALTER TABLE `tb_venda`
  ADD PRIMARY KEY (`ID_venda`);

--
-- Índices de tabela `tb_venda_item`
--
ALTER TABLE `tb_venda_item`
  ADD PRIMARY KEY (`id_item`),
  ADD KEY `id_venda` (`id_venda`),
  ADD KEY `id_produto` (`id_produto`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `tb_caixa`
--
ALTER TABLE `tb_caixa`
  MODIFY `ID_caixa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `tb_categoria`
--
ALTER TABLE `tb_categoria`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de tabela `tb_produto`
--
ALTER TABLE `tb_produto`
  MODIFY `id_produto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT de tabela `tb_usuario`
--
ALTER TABLE `tb_usuario`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `tb_venda`
--
ALTER TABLE `tb_venda`
  MODIFY `ID_venda` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de tabela `tb_venda_item`
--
ALTER TABLE `tb_venda_item`
  MODIFY `id_item` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `tb_caixa`
--
ALTER TABLE `tb_caixa`
  ADD CONSTRAINT `fk_caixa_venda` FOREIGN KEY (`id_venda`) REFERENCES `tb_venda` (`ID_venda`);

--
-- Restrições para tabelas `tb_venda_item`
--
ALTER TABLE `tb_venda_item`
  ADD CONSTRAINT `tb_venda_item_ibfk_1` FOREIGN KEY (`id_venda`) REFERENCES `tb_venda` (`ID_venda`),
  ADD CONSTRAINT `tb_venda_item_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `tb_produto` (`id_produto`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
