// DataInitializer.java
package com.example.geek_denki_development.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.entity.Category;
import com.example.geek_denki_development.entity.Manufacturer;
import com.example.geek_denki_development.entity.Permission;
import com.example.geek_denki_development.entity.Product;
import com.example.geek_denki_development.entity.Role;
import com.example.geek_denki_development.entity.Store;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.CategoryRepository;
import com.example.geek_denki_development.repository.ManufacturerRepository;
import com.example.geek_denki_development.repository.PermissionRepository;
import com.example.geek_denki_development.repository.ProductRepository;
import com.example.geek_denki_development.repository.RoleRepository;
import com.example.geek_denki_development.repository.StoreRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            AdministratorRepository administratorRepository,
            StoreRepository storeRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ManufacturerRepository manufacturerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 店舗データの初期化
            Store store1 = new Store();
            store1.setName("Geek電機 渋谷店");
            store1.setAddress("東京都渋谷区Geek坂 1-1-1");
            storeRepository.save(store1);

            Store store2 = new Store();
            store2.setName("Geek電機 新宿店");
            store2.setAddress("東京都新宿区西Geek 1-1-1");
            storeRepository.save(store2);

            Store store3 = new Store();
            store3.setName("Geek電機 池袋店");
            store3.setAddress("東京都豊島区GeekShine通り 1-1-1");
            storeRepository.save(store3);

            // 役職データの初期化
            String[] roleNames = {"店長", "副店長", "マネージャー", "一般従業員", "パート・アルバイト"};
            for (String roleName : roleNames) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }

            // 権限データの初期化
            String[] permissionNames = {"管理者", "一般"};
            for (String permissionName : permissionNames) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permissionRepository.save(permission);
            }

            // 管理者アカウントの作成
            Administrator admin = new Administrator();
            admin.setStoreId(1L);
            admin.setFirstName("管理者");
            admin.setLastName("テスト");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(passwordEncoder.encode("password123"));
            admin.setRoleId(1L);
            admin.setPermissionId(1L);
            admin.setPhoneNumber("0312345678");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            administratorRepository.save(admin);

            // カテゴリ初期化
            initializeCategories(categoryRepository);

            // メーカー初期化（既存の処理例）
            Manufacturer manuA = new Manufacturer();
            manuA.setName("メーカーA");
            manuA.setCreatedAt(LocalDateTime.now());
            manuA.setUpdatedAt(LocalDateTime.now());
            manufacturerRepository.save(manuA);

            // マスタデータの反映（1行目～10行目のデータのみ初期化）
            initializeMasterProducts(productRepository, categoryRepository, manufacturerRepository);

            System.out.println("初期データを登録しました");
        };
    }

 // DataInitializer.java（該当部分）
    private void initializeCategories(CategoryRepository categoryRepository) {
        // 既存のカテゴリデータ削除
        categoryRepository.deleteAll();
        
        // 大カテゴリ
        Category catLarge1 = new Category();
        catLarge1.setName("冷蔵庫・洗濯機・掃除機");
        catLarge1.setLevel(1);
        categoryRepository.save(catLarge1);
        
        Category catLarge2 = new Category();
        catLarge2.setName("電子レンジ・炊飯器");
        catLarge2.setLevel(1);
        categoryRepository.save(catLarge2);
        
        Category catLarge3 = new Category();
        catLarge3.setName("エアコン・空調");
        catLarge3.setLevel(1);
        categoryRepository.save(catLarge3);
        
        Category catLarge4 = new Category();
        catLarge4.setName("テレビ・レコーダー");
        catLarge4.setLevel(1);
        categoryRepository.save(catLarge4);
        
        // ──「冷蔵庫・洗濯機・掃除機」内の中カテゴリ
        Category medium1 = new Category();
        medium1.setName("冷蔵庫・冷凍庫");
        medium1.setLevel(2);
        medium1.setParent(catLarge1);
        categoryRepository.save(medium1);
        
        Category medium2 = new Category();
        medium2.setName("洗濯機・洗濯乾燥機");
        medium2.setLevel(2);
        medium2.setParent(catLarge1);
        categoryRepository.save(medium2);
        
        Category medium3 = new Category();
        medium3.setName("掃除機・クリーナー");
        medium3.setLevel(2);
        medium3.setParent(catLarge1);
        categoryRepository.save(medium3);
        
        // ──中カテゴリ「冷蔵庫・冷凍庫」内の小カテゴリ
        String[] smallNames1 = {"冷蔵庫", "冷凍庫", "保冷・冷温ボックス", "製氷機", "冷蔵庫関連品"};
        for (String name : smallNames1) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium1);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「洗濯機・洗濯乾燥機」内の小カテゴリ
        String[] smallNames2 = {"洗濯機・洗濯乾燥機", "縦型洗濯機", "2槽式洗濯機", "ハンディ・小型洗濯機", "衣類乾燥機", "洗濯機関連品", "衣類乾燥機・関連品"};
        for (String name : smallNames2) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium2);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「掃除機・クリーナー」内の小カテゴリ
        String[] smallNames3 = {"スティッククリーナー", "サイクロン式掃除機", "紙パック式掃除機", "ロボット掃除機", "ハンディクリーナー"};
        for (String name : smallNames3) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium3);
            categoryRepository.save(small);
        }
        
        // ──「電子レンジ・炊飯器」内の中カテゴリ
        Category medium4 = new Category();
        medium4.setName("オーブンレンジ・電子レンジ");
        medium4.setLevel(2);
        medium4.setParent(catLarge2);
        categoryRepository.save(medium4);
        
        Category medium5 = new Category();
        medium5.setName("炊飯器");
        medium5.setLevel(2);
        medium5.setParent(catLarge2);
        categoryRepository.save(medium5);
        
        // ──中カテゴリ「オーブンレンジ・電子レンジ」内の小カテゴリ
        String[] smallNames4 = {"電子レンジ", "オーブンレンジ", "スチームオーブンレンジ"};
        for (String name : smallNames4) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium4);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「炊飯器」内の小カテゴリ
        String[] smallNames5 = {"炊飯器", "保温ジャー", "ガス炊飯器"};
        for (String name : smallNames5) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium5);
            categoryRepository.save(small);
        }
        
        // ──「エアコン・空調」内の中カテゴリ
        Category medium6 = new Category();
        medium6.setName("エアコン・窓用エアコン");
        medium6.setLevel(2);
        medium6.setParent(catLarge3);
        categoryRepository.save(medium6);
        
        Category medium7 = new Category();
        medium7.setName("扇風機・サーキュレーター");
        medium7.setLevel(2);
        medium7.setParent(catLarge3);
        categoryRepository.save(medium7);
        
        Category medium8 = new Category();
        medium8.setName("暖房器具");
        medium8.setLevel(2);
        medium8.setParent(catLarge3);
        categoryRepository.save(medium8);
        
        // ──中カテゴリ「エアコン・窓用エアコン」内の小カテゴリ
        String[] smallNames6 = {"エアコン", "窓用エアコン", "エアコン関連品"};
        for (String name : smallNames6) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium6);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「扇風機・サーキュレーター」内の小カテゴリ
        String[] smallNames7 = {"リビング扇風機", "タワー型扇風機", "羽根無し扇風機", "サーキュレーター", "携帯型扇風機", "卓上型扇風機"};
        for (String name : smallNames7) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium7);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「暖房器具」内の小カテゴリ
        String[] smallNames8 = {"電気ファンヒーター", "電気ストーブ", "セラミックヒーター", "こたつ・こたつ布団", "ホットカーペット・関連品"};
        for (String name : smallNames8) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium8);
            categoryRepository.save(small);
        }
        
        // ──「テレビ・レコーダー」内の中カテゴリ
        Category medium9 = new Category();
        medium9.setName("テレビ");
        medium9.setLevel(2);
        medium9.setParent(catLarge4);
        categoryRepository.save(medium9);
        
        Category medium10 = new Category();
        medium10.setName("レコーダー");
        medium10.setLevel(2);
        medium10.setParent(catLarge4);
        categoryRepository.save(medium10);
        
        Category medium11 = new Category();
        medium11.setName("プロジェクター");
        medium11.setLevel(2);
        medium11.setParent(catLarge4);
        categoryRepository.save(medium11);
        
        // ──中カテゴリ「テレビ」内の小カテゴリ
        String[] smallNames9 = {"液晶テレビ", "有機ELテレビ", "ポータブルテレビ", "テレビ関連品"};
        for (String name : smallNames9) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium9);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「レコーダー」内の小カテゴリ
        String[] smallNames10 = {"ブルーレイレコーダー", "HDDレコーダー", "レコーダー関連品"};
        for (String name : smallNames10) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium10);
            categoryRepository.save(small);
        }
        
        // ──中カテゴリ「プロジェクター」内の小カテゴリ
        String[] smallNames11 = {"プロジェクター本体", "プロジェクター関連品", "プロジェクタースクリーン", "プロジェクタースクリーン関連品"};
        for (String name : smallNames11) {
            Category small = new Category();
            small.setName(name);
            small.setLevel(3);
            small.setParent(medium11);
            categoryRepository.save(small);
        }
    }


    // マスタデータの反映処理（1行目～10行目としてマスタデータを登録）
 // DataInitializer.java (抜粋)
    private void initializeMasterProducts(ProductRepository productRepository,
                                            CategoryRepository categoryRepository,
                                            ManufacturerRepository manufacturerRepository) {
        // 1行目: SR-IKJ-01 -> 冷蔵庫
    	Manufacturer manu1 = getOrCreateManufacturer("三角電機", manufacturerRepository);
        // 対象は小カテゴリなので level 3 を指定
        Category cat1 = categoryRepository.findByNameAndLevel("冷蔵庫", 3);
        createMasterProduct("SR-IKJ-01", "SR-IKJ-01", "いい感じの冷蔵庫です", new BigDecimal("0"), 0,
                null, cat1, manu1, productRepository);
        
        // 2行目: YDC-DSK-1001 -> ドラム式洗濯乾燥機
        Manufacturer manu2 = getOrCreateManufacturer("夕立", manufacturerRepository);
        Category cat2 = categoryRepository.findByNameAndLevel("ドラム式洗濯乾燥機", 3);
        createMasterProduct("YDC-DSK-1001", "YDC-DSK-1001", "いい感じのドラム式洗濯乾燥機です", new BigDecimal("0"), 0,
                null, cat2, manu2, productRepository);
        
        // 3行目: danson-slim-01 -> サイクロン式掃除機
        Manufacturer manu3 = getOrCreateManufacturer("ダンソン", manufacturerRepository);
        Category cat3 = categoryRepository.findByNameAndLevel("サイクロン式掃除機", 3);
        createMasterProduct("danson-slim-01", "danson-slim-01", "いい感じのサイクロン式掃除機です", new BigDecimal("0"), 0,
                null, cat3, manu3, productRepository);
        
        // 4行目: FD-1221 -> 電子レンジ
        Manufacturer manu4 = getOrCreateManufacturer("Fanasonic", manufacturerRepository);
        Category cat4 = categoryRepository.findByNameAndLevel("電子レンジ", 3);
		 // 対象は小カテゴリなので level 3 を指定
        createMasterProduct("FD-1221", "FD-1221", "いい感じの電子レンジです", new BigDecimal("0"), 0,
                null, cat4, manu4, productRepository);
        
        // 5行目: FS-2001 -> 炊飯器
        Manufacturer manu5 = getOrCreateManufacturer("Fanasonic", manufacturerRepository);
        Category cat5 = categoryRepository.findByNameAndLevel("炊飯器", 3);
        createMasterProduct("FS-2001", "FS-2001", "いい感じの炊飯器です", new BigDecimal("0"), 0,
                null, cat5, manu5, productRepository);
        
        // 6行目: DN-SOR-d1 -> スチームオーブンレンジ
        Manufacturer manu6 = getOrCreateManufacturer("DALNUDA", manufacturerRepository);
        Category cat6 = categoryRepository.findByNameAndLevel("スチームオーブンレンジ", 3);
        createMasterProduct("DN-SOR-d1", "DN-SOR-d1", "いい感じのスチームオーブンレンジです", new BigDecimal("0"), 0,
                null, cat6, manu6, productRepository);
        
        // 7行目: N-ETV-1111 -> 液晶テレビ
        Manufacturer manu7 = getOrCreateManufacturer("西芝", manufacturerRepository);
        Category cat7 = categoryRepository.findByNameAndLevel("液晶テレビ", 3);
        createMasterProduct("N-ETV-1111", "N-ETV-1111", "いい感じの液晶テレビです", new BigDecimal("0"), 0,
                null, cat7, manu7, productRepository);
        
        // 8行目: Poomba-01 -> ロボット掃除機
        Manufacturer manu8 = getOrCreateManufacturer("gRobot", manufacturerRepository);
        Category cat8 = categoryRepository.findByNameAndLevel("ロボット掃除機", 3);
        createMasterProduct("Poomba-01", "Poomba-01", "いい感じのロボット掃除機です", new BigDecimal("0"), 0,
                null, cat8, manu8, productRepository);
        
        // 9行目: MT-S01 -> 炊飯器
        Manufacturer manu9 = getOrCreateManufacturer("マイリス・トーヤマ", manufacturerRepository);
        Category cat9 = categoryRepository.findByNameAndLevel("炊飯器", 3);
        createMasterProduct("MT-S01", "MT-S01", "いい感じの炊飯器です", new BigDecimal("0"), 0,
                null, cat9, manu9, productRepository);
        
        // 10行目: TZ-HJ-11 -> 保温ジャー
        Manufacturer manu10 = getOrCreateManufacturer("虎印", manufacturerRepository);
        Category cat10 = categoryRepository.findByNameAndLevel("保温ジャー", 3);
        createMasterProduct("TZ-HJ-11", "TZ-HJ-11", "いい感じの保温ジャーです", new BigDecimal("0"), 0,
                null, cat10, manu10, productRepository);
    }


    private Manufacturer getOrCreateManufacturer(String name, ManufacturerRepository manufacturerRepository) {
        return manufacturerRepository.findAll()
                .stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseGet(() -> {
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setName(name);
                    manufacturer.setCreatedAt(LocalDateTime.now());
                    manufacturer.setUpdatedAt(LocalDateTime.now());
                    return manufacturerRepository.save(manufacturer);
                });
    }

    private void createMasterProduct(String name, String modelNumber, String description,
                                     BigDecimal price, Integer stockQuantity, String imageUrl,
                                     Category category, Manufacturer manufacturer,
                                     ProductRepository repository) {
        Product product = new Product();
        product.setName(name);
        product.setModelNumber(modelNumber);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        repository.save(product);
    }
}
