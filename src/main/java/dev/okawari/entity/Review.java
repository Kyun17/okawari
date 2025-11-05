@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    
    private int id;
    private String content;
    private int rating;
}