import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class FiltrandoImg {

    public static int[][][] lerImagem(String nomeArquivo) throws IOException {
        File input = new File(nomeArquivo);
        BufferedImage imagem = ImageIO.read(input);

        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // matriz com as posições dos pixels e suas cores em rgb
        int[][][] result = new int[altura][largura][3];

        for (int linha = 0; linha < altura; linha++) {
            for (int coluna = 0; coluna < largura; coluna++) {
                int pixel = imagem.getRGB(coluna, linha);
                // Obtem a cor a partir dos valores do pixels
                Color color = new Color(pixel, true);
                // Obtem os valore de R G B (red-green-blue vermelho-verde-azul)
                result[linha][coluna][0] = color.getRed();
                result[linha][coluna][1] = color.getGreen();
                result[linha][coluna][2] = color.getBlue();
            }
        }

        return result;
    }

    public static void imprimeMatriz(int[][][] matriz){
        for(int i = 0; i < matriz.length; i ++){
            for(int j = 0; j < matriz[0].length; j ++){
                System.out.print(matriz[i][j][0] + " ");
            }System.out.println();
        }
    }


    //pega todos os valores ao redor de uma posição x, y e retorna-os em uma matriz
    public static int[][][] pegaRedores(int[][][] matrix, int[] posicao) {
        int[][][] res = new int[3][3][3];

        int[][] indices = { { -1, -1 }, { -1, 0}, { -1, 1 }, { 0, -1 }, {0,0}, { 0, 1 }, {1, -1}, { 1, 0 }, { 1, 1 }};

        for (int[] is : indices) {
            try {
                int x = is[0];
                int y = is[1];

                int redorX = posicao[0] + x;
                int redorY = posicao[1] + y;

                res[x + 1][y + 1][0] = matrix[redorX][redorY][0];
                res[x + 1][y + 1][1] = matrix[redorX][redorY][1];
                res[x + 1][y + 1][2] = matrix[redorX][redorY][2];

            } catch (IndexOutOfBoundsException e) {

            }

        }
        return res;

    }

    
    public static void salvarImagem(int[][][] result, String nomeArquivo) throws IOException {
        //Imagem que sera escrita em forma de output para o caminho atual
        BufferedImage image = new BufferedImage(result[0].length, result.length, BufferedImage.TYPE_INT_RGB);

        //passa por cada pixel setando uma cor, passando pra image que vai transformar em pixel colorido e armazendando
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                Color cor = new Color(result[i][j][0], result[i][j][1], result[i][j][2]);
                image.setRGB(j, i, cor.getRGB());
            }
        }

        //cria o arquivo
        File output = new File(nomeArquivo);

        //escreve a imagem dentro do arquivo recem criado
        ImageIO.write(image, "jpg", output);
    }

    //vai sobrepor um kernel (matriz de convolução) sobre uma matriz de imagem que temos
    //mudando os valores e retornando uma nova matriz de imagem 

    public static int[][][] filtering(int[][] kernel, int[][][] matriz){
        int[][][] resultado = new int[matriz.length - 2][matriz[0].length - 2][3];
        
        for(int i = 1; i < matriz.length - 1; i ++){
            for(int j = 1; j < matriz[0].length - 1; j ++){
                int[] posicao = {i, j};
                int[][][] redores = pegaRedores(matriz, posicao);

                int redFiltrado = 0;
                int greenFiltrado = 0;
                int blueFiltrado = 0;

                for (int k = 0; k < 3; k ++) {
                    for (int l = 0; l < 3; l ++) {
                        int red = redores[k][l][0];
                        int green = redores[k][l][1];
                        int blue = redores[k][l][2];

                        redFiltrado += red * kernel[k][l];
                        greenFiltrado += green * kernel[k][l];
                        blueFiltrado += blue * kernel[k][l];

                    }
                }

                if(redFiltrado < 0){
                    redFiltrado = 0;
                }

                if(greenFiltrado < 0){
                    greenFiltrado = 0;
                }

                if(blueFiltrado < 0){
                    blueFiltrado = 0;
                }

                resultado[i - 1][j - 1][0] = redFiltrado / 9;
                resultado[i - 1][j - 1][1] = greenFiltrado / 9;
                resultado[i - 1][j - 1][2] = blueFiltrado / 9;

            }
        }

        return resultado;
    }


    public static void main(String[] args) {

        try {
            int[][][] matrix = lerImagem("Img1.png"); 

            //kernel de borda
            int[][] kernelBorbas =  {{-1,-1,-1}, 
                                    {-1, 8, -1}, 
                                    {-1,-1,-1}};

            int[][][] bordas = filtering(kernelBorbas, matrix);

            salvarImagem(bordas, "bordas.jpg");

            matrix = lerImagem("Img2.png");

            //kernel de desfoque
            int[][] kernelDesfoque= {{1,1,1}, 
                                    {1, 1, 1}, 
                                    {1,1,1}};

            int[][][] desfoque = filtering(kernelDesfoque, matrix);

            salvarImagem(desfoque, "desfoque.jpg");

            //kernel de shader
            int[][] kernelShader =  {{0,-1,0}, 
                                    {-1, 5, -1}, 
                                    {0,-1,0}};

            int[][][] shader = filtering(kernelShader, matrix);

            salvarImagem(shader, "shader.jpg");



        } catch (IOException e) {
            System.out.println("Erro ao pegar o arquivo de imagem!!");
        }
    }

}
