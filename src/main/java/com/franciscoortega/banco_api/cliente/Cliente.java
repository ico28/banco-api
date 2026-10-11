@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documento;
    private String nombre;
    private String apellidos;
    private String email;

    @Column(name = "fecha_alta")
    private OffsetDateTime fechaAlta;
}